package com.wangwren.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.CookieUtils;
import com.wangwren.pojo.TbUser;
import com.wangwren.sso.service.UserService;

/**
 * 用户相关表现层
 * @author wwr
 *
 */
@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	
	/**
	 * 注册时检查数据有效性
	 * @param param 用户名、电话、邮箱
	 * @param type 1代表用户名、2代表电话、3代表邮箱
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public LetaoResult checkUserData(@PathVariable String param,@PathVariable Integer type) throws Exception{
		LetaoResult result = userService.checkUserData(param, type);
		return result;
	}
	
	/**
	 * 用户注册
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public LetaoResult register(TbUser user) throws Exception{
		LetaoResult result = userService.register(user);
		return result;
	}
	
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public LetaoResult login(String username,String password,HttpServletRequest request,HttpServletResponse response) throws Exception{
		LetaoResult result = userService.login(username, password);
		//登录成功后，将token放入cookid中，方便其他系统访问时获取token进而在redis中获取用户信息
		if(result.getStatus() == 200) {
			CookieUtils.setCookie(request, response, TOKEN_KEY, result.getData().toString());
		}
		return result;
	}
	
	/**
	 * 通过token查询用户信息
	 * @return
	 * @throws Exception
	 */
	/*@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET)
	@ResponseBody
	public LetaoResult getUserByToken(@PathVariable String token) throws Exception{
		LetaoResult result = userService.getUserByToken(token);
		return result;
	}*/
	/**
	 * jsonp第二种写法，第一种写法看首页分类的显示，在manager-server中写的
	 * jsonp不是一个技术，而是利用了js的特点，避免了js跨域的问题
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET)
	@ResponseBody
	public Object getUserByToken(@PathVariable String token,String callback) throws Exception{
		LetaoResult result = userService.getUserByToken(token);
		//判断是否为jsonp请求
		if(StringUtils.isNotBlank(callback)) {
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			//设置回调方法
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}
	
	/**
	 * 用户退出
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/logout/{token}",method=RequestMethod.GET)
	@ResponseBody
	public Object logout(@PathVariable String token,String callback) throws Exception{
		LetaoResult result = userService.logout(token);
		if(StringUtils.isNotBlank(callback)) {
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			//设置回调方法
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}
}
