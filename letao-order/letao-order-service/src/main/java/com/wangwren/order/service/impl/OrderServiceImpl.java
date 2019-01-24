package com.wangwren.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.IDUtils;
import com.wangwren.mapper.TbOrderItemMapper;
import com.wangwren.mapper.TbOrderMapper;
import com.wangwren.mapper.TbOrderShippingMapper;
import com.wangwren.order.jedis.JedisClient;
import com.wangwren.order.pojo.OrderInfo;
import com.wangwren.order.service.OrderService;
import com.wangwren.pojo.TbOrderItem;
import com.wangwren.pojo.TbOrderShipping;
/**
 * 订单处理
 * @author wwr
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	/**
	 * redis中订单号的key
	 */
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	
	/**
	 * 订单号自动增长的初始值
	 */
	@Value("${ORDER_ID_BEGIN_VALUE}")
	private String ORDER_ID_BEGIN_VALUE;
	
	/**
	 * 订单详情在redis中的key
	 */
	@Value("${ORDER_ITEM_ID_GEN_KEY}")
	private String ORDER_ITEM_ID_GEN_KEY;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	/**
	 * 创建订单，返回订单号
	 * @return
	 */
	@Override
	public LetaoResult createOrder(OrderInfo orderInfo) {

		//生成订单号，使用时间戳(取当前时间的毫秒值)再加上redis的自动增长
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			//如果key不存在，则设置初始值
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_BEGIN_VALUE);
		}
		Long genOrderId = IDUtils.genOrderId();
		//自增
		String reOderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		//订单id
		String orderId = genOrderId.toString() + reOderId;
		//订单信息完善
		orderInfo.setOrderId(orderId);
		//全场包邮
		orderInfo.setPostFee("0");
		//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		//订单创建更新时间
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		//将订单信息插入表中
		orderMapper.insert(orderInfo);
		
		//从OrderInfo中获取OrderItem订单的商品信息
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		//循环完善信息，插入数据库中
		for (TbOrderItem orderItem : orderItems) {
			//获得明细主键
			String otId = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
			orderItem.setId(otId);
			orderItem.setOrderId(orderId);
			
			//插入数据库
			orderItemMapper.insert(orderItem);
		}
		
		//获取收货地址信息，完善
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		//插入表中
		orderShippingMapper.insert(orderShipping);
		
		//最后返回订单号
		return LetaoResult.ok(orderId);
	}

}
