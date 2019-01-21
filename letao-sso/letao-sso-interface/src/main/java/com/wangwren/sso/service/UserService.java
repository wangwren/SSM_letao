package com.wangwren.sso.service;
/**
 * 用户相关接口
 * @author wwr
 *
 */

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.pojo.TbUser;

public interface UserService {

	/**
	 * 注册时检查参数有效性
	 * @param param 要检查的数据
	 * @param type 数据类型：1代表用户名，2代表电话，3代表email
	 * @return
	 */
	public LetaoResult checkUserData(String param,Integer type);
	
	/**
	 * 用户注册接口
	 * @param user
	 * @return
	 */
	public LetaoResult register(TbUser user);
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 */
	public LetaoResult login(String username,String password);
	
	/**
	 * 通过token查询用户信息
	 * @param token
	 * @return
	 */
	public LetaoResult getUserByToken(String token);
	
	/**
	 * 用户退出
	 * @param token
	 * @return
	 */
	public LetaoResult logout(String token);
}
