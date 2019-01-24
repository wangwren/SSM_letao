package com.wangwren.order.pojo;

import java.io.Serializable;
import java.util.List;

import com.wangwren.pojo.TbOrder;
import com.wangwren.pojo.TbOrderItem;
import com.wangwren.pojo.TbOrderShipping;

/**
 * 封装订单对象，用于接收前台界面中的信息
 * @author wwr
 *
 */
public class OrderInfo extends TbOrder implements Serializable {

	/**
	 * 多个商品详情，一个订单可以对应多个商品
	 */
	private List<TbOrderItem> orderItems;
	
	/**
	 * 收货地址
	 */
	private TbOrderShipping orderShipping;

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}

	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	
}
