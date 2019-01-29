package com.wangwren.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wangwren.order.pojo.OrderInfo;
import com.wangwren.order.service.UserOrderService;
import com.wangwren.pojo.TbUser;

@Controller
public class UserOrderController {

	@Autowired
	private UserOrderService userOrderService;
	
	/**
	 * 根据用户id查询用户
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/my/order")
	public String myOrder(HttpServletRequest request,Model model) throws Exception{
		
		TbUser user = (TbUser) request.getAttribute("user");
		
		List<OrderInfo> orders = userOrderService.getOrdersByUserId(user.getId());
		
		model.addAttribute("orders", orders);
		
		return "my-orders";
	}
}
