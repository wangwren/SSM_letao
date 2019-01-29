package com.wangwren.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangwren.mapper.TbOrderItemMapper;
import com.wangwren.mapper.TbOrderMapper;
import com.wangwren.mapper.TbOrderShippingMapper;
import com.wangwren.order.pojo.OrderInfo;
import com.wangwren.order.service.UserOrderService;
import com.wangwren.pojo.TbOrder;
import com.wangwren.pojo.TbOrderExample;
import com.wangwren.pojo.TbOrderExample.Criteria;
import com.wangwren.pojo.TbOrderItem;
import com.wangwren.pojo.TbOrderItemExample;
/**
 * 我的订单
 * @author wwr
 *
 */
@Service
public class UserOrderServiceImpl implements UserOrderService {

	@Autowired
	private TbOrderMapper tbOrderMapper;
	
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	
	@Autowired
	private TbOrderShippingMapper tbOrderShippingMapper;
	
	/**
	 * 根据用户id查询订单
	 */
	@Override
	public List<OrderInfo> getOrdersByUserId(Long userId) {

		List<OrderInfo> list = new ArrayList<>();
		
		//查询出订单信息
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<TbOrder> orderList = tbOrderMapper.selectByExample(example);
		
		//将数据复制到OrderInfo中
		for(int i = 0 ; i < orderList.size() ; i++) {
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setOrderId(orderList.get(i).getOrderId());
			orderInfo.setPayment(orderList.get(i).getPayment());
			orderInfo.setPaymentType(orderList.get(i).getPaymentType());
			orderInfo.setPostFee(orderList.get(i).getPostFee());
			orderInfo.setStatus(orderList.get(i).getStatus());
			orderInfo.setCreateTime(orderList.get(i).getCreateTime());
			orderInfo.setUpdateTime(orderList.get(i).getUpdateTime());
			orderInfo.setUserId(orderList.get(i).getUserId());
			
			list.add(orderInfo);
		}
		
		//再通过订单id查出订单详细信息
		TbOrderItemExample example2 = new TbOrderItemExample();
		for (OrderInfo orderInfo : list) {
			com.wangwren.pojo.TbOrderItemExample.Criteria criteria2 = example2.createCriteria();
			criteria2.andOrderIdEqualTo(orderInfo.getOrderId());
			List<TbOrderItem> orderItemList = tbOrderItemMapper.selectByExample(example2);
			//将对应订单编号的详细商品信息加入
			orderInfo.setOrderItems(orderItemList);
		}
		
		return list;
	}

}
