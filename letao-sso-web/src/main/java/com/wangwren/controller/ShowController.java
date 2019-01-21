package com.wangwren.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面展示
 * @author wwr
 *
 */
@Controller
public class ShowController {

	/**
	 * 用户登录界面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/page/login")
	public String login() throws Exception{
		return "login";
	}
	
	/**
	 * 用户注册界面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/page/register")
	public String register() throws Exception{
		return "register";
	}
}
