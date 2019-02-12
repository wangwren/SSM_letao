package com.wangwren.order.service;
/**
 * 订单处理
 * @author wwr
 *
 */

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.order.pojo.OrderInfo;

public interface OrderService {

	/**
	 * 创建订单，返回订单号
	 * @return
	 */
	public LetaoResult createOrder(OrderInfo orderInfo);
	
	/**
	 * 支付宝支付
	 * @param orderId
	 * @param userId
	 * @return
	 */
	public LetaoResult pay(String orderId,Long userId);
}
