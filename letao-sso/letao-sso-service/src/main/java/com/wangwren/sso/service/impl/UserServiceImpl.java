package com.wangwren.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.mapper.TbUserMapper;
import com.wangwren.pojo.TbUser;
import com.wangwren.pojo.TbUserExample;
import com.wangwren.pojo.TbUserExample.Criteria;
import com.wangwren.sso.jedis.JedisClient;
import com.wangwren.sso.service.UserService;

/**
 * 用户接口实现类
 * @author wwr
 *
 */
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_TOKEN}")
	private String SESSION_TOKEN;
	
	/**
	 * 有效时间
	 */
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	/**
	 * 注册时检查参数有效性，即数据库中是否存在该值
	 * @param param 要检查的数据
	 * @param type 数据类型：1代表用户名，2代表电话，3代表email
	 * 
	 * 返回数据格式：
	 * 	{
			status: 200 //200 成功
			msg: "OK" // 返回信息消息
			data: false // 返回数据，true：数据可用，false：数据不可用
		}
	 * @return
	 */
	@Override
	public LetaoResult checkUserData(String param, Integer type) {
		
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		if(type == 1) {
			//校验用户名是否存在
			criteria.andUsernameEqualTo(param);
		}else if(type == 2) {
			//校验电话是否存在
			criteria.andPhoneEqualTo(param);
		}else if(type == 3) {
			//校验email是否存在
			criteria.andEmailEqualTo(param);
		}else {
			return LetaoResult.build(500, "参数非法!!!");
		}
		
		//查询数据
		List<TbUser> list = userMapper.selectByExample(example);
		if(list != null && list.size() > 0) {
			//即能查询到数据，说明以上三种类型中至少有一种已经注册过了，不允许再注册，返回数据不可用
			return LetaoResult.ok(false);
		}
		//数据可用
		return LetaoResult.ok(true);
	}

	/**
	 * 用户注册
	 * 返回数据格式示例：
	 * 	{
			status: 400
			msg: "注册失败. 请校验数据后请再提交数据."
			data: null
		}

	 */
	@Override
	public LetaoResult register(TbUser user) {

		//数据校验，虽然有了前台js校验，但是为了防止某些人躲避js，后台还需要校验
		if(StringUtils.isBlank(user.getUsername())) {
			//如果用户名为空
			return LetaoResult.build(400, "注册失败，用户名不能为空！！！");
		}
		
		//校验用户名是否重复
		if(!((boolean)checkUserData(user.getUsername(), 1).getData())) {
			//如果用户名重复
			return LetaoResult.build(400, "注册失败，用户名重复！！！");
		}
		
		//校验手机号是否重复
		if(StringUtils.isNotBlank(user.getPhone())) {
			//如果手机号不为空则校验
			if(!((boolean)checkUserData(user.getPhone(), 2).getData())) {
				//如果手机号重复
				return LetaoResult.build(400, "注册失败，手机号重复！！！");
			}
		}
		//校验email是否重复
		if(StringUtils.isNotBlank(user.getEmail())) {
			//如果Email不为空则校验
			if(!((boolean)checkUserData(user.getEmail(), 3).getData())) {
				//如果email重复
				return LetaoResult.build(400, "注册失败，email重复！！！");
			}
		}
		
		//补全pojo
		user.setCreated(new Date());
		user.setUpdated(new Date());
		
		//密码加密，使用Spring核心库中提供的md5加密
		String md5Pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pwd);
		
		//存入数据库
		userMapper.insert(user);
		
		return LetaoResult.ok();
	}

	/**
	 * 用户登录
	 * 如果不存在返回错误数据
	 * 如果存在，使用UUID生成一个token数据，之后存在redis中，模拟session，以便在别的系统中也可以获取信息
	 * 
	 * 登录成功后返回的示例：
	 * 	{
			status: 200
			msg: "OK"
			data: "fe5cb546aeb3ce1bf37abcb08a40493e" //登录成功，返回token
		}
	 */
	@Override
	public LetaoResult login(String username, String password) {
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return LetaoResult.build(400, "用户名或密码不能为空！！！");
		}
		//通过用户名查，先看用户名是否能查到，再看密码
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		//如果没有该用户名
		if(list == null || list.size() == 0) {
			return LetaoResult.build(400, "用户名或密码错误！！！");
		}
		//取出用户信息
		TbUser user = list.get(0);
		//如果有用户名，则比较密码
		if(!(DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword()))) {
			//密码不正确
			return LetaoResult.build(400, "用户名或密码错误！！！");
		}
		
		//用户名密码都正确，生成token，存入redis中
		String token = UUID.randomUUID().toString();
		//将用户信息的密码设为空后存入redis，保证安全
		user.setPassword(null);
		jedisClient.set(SESSION_TOKEN + ":" + token, JsonUtils.objectToJson(user));
		//设置key的有效时间为半个小时
		jedisClient.expire(SESSION_TOKEN + ":" + token, SESSION_EXPIRE);
		
		//返回数据，将token带回去
		return LetaoResult.ok(token);
	}

	/**
	 * 通过token查询用户信息，从redis中去查询
	 * 返回数据示例：
	 * {
			status: 200
			msg: "OK"
			data: {"id":1,"username":"zhangzhijun","phone":"15800807944",
			"email":"420840806@qq.com","created":1414119176000,"updated":1414119179000}"
		}
	 */
	@Override
	public LetaoResult getUserByToken(String token) {

		String json = jedisClient.get(SESSION_TOKEN + ":" + token);
		if(StringUtils.isBlank(json)) {
			//没有查到数据
			return LetaoResult.build(400, "请重新登录！！！");
		}
		//重新设置session的有效期
		jedisClient.expire(SESSION_TOKEN + ":" + token, SESSION_EXPIRE);
		//将json数据转成java对象，不要将json直接返回，这样输出的要求与接口文档中要求的不符
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		
		return LetaoResult.ok(user);
	}

	/**
	 * 用户退出，即从redis中删除该key
	 */
	@Override
	public LetaoResult logout(String token) {
		jedisClient.del(SESSION_TOKEN + ":" + token);
		return LetaoResult.ok();
	}

}
