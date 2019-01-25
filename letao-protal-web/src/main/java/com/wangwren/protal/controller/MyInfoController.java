package com.wangwren.protal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 我的乐淘
 * @author wwr
 *
 */
@Controller
public class MyInfoController {

	@RequestMapping("/show/myinfo")
	public String showMyInfo() throws Exception{
		
		return "my-info";
	}
}
