package com.wangwren.order.service;

import java.util.List;

import com.wangwren.order.pojo.OrderInfo;

/**
 * 查询用户订单
 * @author wwr
 *
 */
public interface UserOrderService {

	/**
	 * 通过用户id查询订单
	 * @return
	 */
	public List<OrderInfo> getOrdersByUserId(Long userId);
}
