package com.wangwren.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShowController {

	/**
	 * 显示后台首页
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/")
	public String showIndex() throws Exception{
		
		return "index";
	}
	
	/**
	 * 显示后台中的每个功能模块
	 * 因为后台点击连接localhost:8080/item-list
	 * 之后的参数链接会变，所以使用带一个参数
	 * @param page
	 * @return
	 */
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page) {
		
		return page;
	}
}
