package com.wangwren.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	public String login(String url,Model model) throws Exception{
		//将url返回至前台界面，用于前台跳转，如果url为空，则跳转到首界面
		model.addAttribute("redirect", url);
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
