package com.wangwren.order.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.CookieUtils;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.order.jedis.JedisClient;
import com.wangwren.order.pojo.OrderInfo;
import com.wangwren.order.service.OrderService;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbUser;

/**
 * 订单处理
 * @author wwr
 *
 */
@Controller
public class OrderCartController {

	/**
	 * 购物车cookie的key
	 */
	@Value("${CART_KEY}")
	private String CART_KEY;
	
	/**
	 * redis中的购物车key
	 */
	@Value("${CART_REIDS_KEY}")
	private String CART_REIDS_KEY;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private JedisClient jedisClient;
	
	/**
	 * 订单确认页面
	 * 该方法执行之前会执行LoginInterceptor拦截器，该拦截器放行后会在request域中存放user信息
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) throws Exception{
		//用户必须是登录状态
		//取用户id
		TbUser user = (TbUser) request.getAttribute("user");
		
		//根据用户信息取收货地址列表，暂时使用静态数据处理
		//把收货地址列表取出传递给页面
		
		//从redis中将购物车中所有商品取出
		List<TbItem> cartList = getCartList2Redis(user);
		
		if(cartList == null || cartList.size() == 0) {
			//如果购物车为空，返回错误界面
			return "error2";
		}
		
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}

	
	/**
	 * 订单结算
	 * 使用request来获取用户id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo orderInfo,HttpServletRequest request,HttpServletResponse response) throws Exception{
		TbUser user = (TbUser) request.getAttribute("user");
		orderInfo.setUserId(user.getId());
		LetaoResult result = orderService.createOrder(orderInfo);
		
		//从redis中将购物车中所有商品取出
		List<TbItem> cartList = getCartList2Redis(user);
		//成功后，删除redis中的购物车
		for (TbItem tbItem : cartList) {
			jedisClient.hdel(CART_REIDS_KEY + ":" + user.getId() + ":BASE", tbItem.getId().toString());
		}
		
		
		//订单号
		request.setAttribute("orderId", result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		
		//预计送达时间，三天后
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		request.setAttribute("date", dateTime.toString("yyyy-MM-dd"));
		
		return "success";
	}
	
	/**
	 * 从cookie中获取购物车列表
	 * @return
	 */
	private List<TbItem> getCartList(HttpServletRequest request){
		//指定cookie名称，并编码
		String json = CookieUtils.getCookieValue(request, CART_KEY, true);
		if(StringUtils.isBlank(json)) {
			//如果为空，则返回一个空的列表
			return new ArrayList<TbItem>();
		}
		//如果不为空，则转成java对象返回
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	/**
	 * 从redis中将购物车中所有商品取出
	 * @param user
	 * @return
	 */
	private List<TbItem> getCartList2Redis(TbUser user) {
		Map<String, String> itemMap = jedisClient.hgetAll(CART_REIDS_KEY + ":" + user.getId() + ":BASE");
		List<TbItem> cartList = new ArrayList<>();
		for (String mapValue : itemMap.values()) {
			TbItem item = JsonUtils.jsonToPojo(mapValue, TbItem.class);
			cartList.add(item);
		}
		return cartList;
	}
}
