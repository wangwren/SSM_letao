package com.wangwren.cart.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.cart.jedis.JedisClient;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.CookieUtils;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbUser;
import com.wangwren.service.ItemService;
import com.wangwren.sso.service.UserService;
/**
 * 购物车相关操作
 * @author wwr
 *
 */
@Controller
public class CartController {

	/**
	 * 购物车cookie的key
	 */
	@Value("${CART_KEY}")
	private String CART_KEY;
	
	/**
	 * 购物车保存时间
	 */
	@Value("${CART_EXPIER}")
	private int CART_EXPIER;
	
	/**
	 * 用户登录的token
	 */
	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	
	/**
	 * 购物车在redis中的id
	 */
	@Value("${CART_REIDS_KEY}")
	private String CART_REIDS_KEY;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * redis
	 */
	@Autowired
	private JedisClient jedisClient;
	
	/**
	 * 向购物车中添加商品
	 * 使用cookie来模拟购物车，不需要登录也可以加入购物车
	 * 购物车中的信息就使用TbItem这个类来表示
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addItem2Cart(@PathVariable Long itemId,@RequestParam(defaultValue="1") Integer num,
											HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		//查看用户是否登录
		TbUser user = getTbUserByToken(request);
		if(user != null) {
			//新加的商品添加到redis中
			System.out.println("添加至redis中");
			
			//先查询redis中是否存在该商品
			String itemStr = jedisClient.hget(CART_REIDS_KEY + ":" + user.getId() + ":BASE", itemId.toString());
			if(StringUtils.isNotBlank(itemStr)) {
				//如果不为空，则存在该商品，将数量改变
				TbItem item = JsonUtils.jsonToPojo(itemStr, TbItem.class);
				item.setNum(item.getNum() + num);
				jedisClient.hset(CART_REIDS_KEY + ":" + user.getId() + ":BASE", itemId.toString(), JsonUtils.objectToJson(item));
				//设置有效时间
				jedisClient.expire(CART_REIDS_KEY + ":" + user.getId() + ":BASE", CART_EXPIER);
				return "cartSuccess";
			}
			//查询出商品
			TbItem item = itemService.findItemById(itemId);
			//重新修改数量
			item.setNum(num);
			//修改图片
			String[] images = item.getImage().split(",");
			item.setImage(images[0]);
			//如果是新添加的商品的，设置近redis中
			jedisClient.hset(CART_REIDS_KEY + ":" + user.getId() + ":BASE", itemId.toString(), JsonUtils.objectToJson(item));
			//设置有效时间
			jedisClient.expire(CART_REIDS_KEY + ":" + user.getId() + ":BASE", CART_EXPIER);
			return "cartSuccess";
		}
		
		//如果用户未登录
		//从cookie中取出购物车列表
		List<TbItem> list = getCartList(request);
		//标志位，用来判断列表中是否存在新加的商品
		boolean flag = false;
		//判断新加入的商品是否存在在列表中
		for (TbItem item : list) {
			//通过值比较
			if(item.getId().longValue() == itemId.longValue()) {
				//如果列表中已经存在该商品，则将数量调整
				item.setNum(item.getNum() + num);
				flag = true;
				//跳出循环
				break;
			}
		}
		//如果不存在就加入购物车列表中
		if(!flag) {
			//去服务层查询出商品信息
			TbItem item = itemService.findItemById(itemId);
			//重新修改数量
			item.setNum(num);
			//修改图片
			String[] images = item.getImage().split(",");
			item.setImage(images[0]);
			
			//加入到列表中
			list.add(item);
		}
		//添加至cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(list), CART_EXPIER, true);
		//返回成功页面
		return "cartSuccess";
	}
	
	
	/**
	 * 展示购物车列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//从cookie中取出购物车列表
		List<TbItem> list = getCartList(request);
		
		//查看用户是否登录，如果登录就将cookie中的商品同步到redis中
		TbUser user = getTbUserByToken(request);
		if(user != null) {
			//同步到redis中
			for (TbItem tbItem : list) {
				Long id = tbItem.getId();
				String itemStr = jedisClient.hget(CART_REIDS_KEY + ":" + user.getId() + ":BASE", id.toString());
				TbItem item = new TbItem();
				if(StringUtils.isNotBlank(itemStr)) {
					//如果该用户在redis中加过该商品，数量加
					item = JsonUtils.jsonToPojo(itemStr, TbItem.class);
					if(id.longValue() == item.getId().longValue()) {
						tbItem.setNum(item.getNum() + tbItem.getNum());
					}
				}
				jedisClient.hset(CART_REIDS_KEY + ":" + user.getId() + ":BASE", id.toString(), JsonUtils.objectToJson(tbItem));
			}
			//设置有效时间
			jedisClient.expire(CART_REIDS_KEY + ":" + user.getId() + ":BASE", CART_EXPIER);
			//同步成功后清空cookie中的购物车
			list.clear();
			CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(list), 0, true);
			
			//同步成功后取出redis中的全部商品列表
			Map<String, String> itemMap = jedisClient.hgetAll(CART_REIDS_KEY + ":" + user.getId() + ":BASE");
			for (String mapValue : itemMap.values()) {
				TbItem item = JsonUtils.jsonToPojo(mapValue, TbItem.class);
				//list同步后被清空了
				list.add(item);
			}
			//将数据返回至前台界面呢
			request.setAttribute("cartList", list);
			//返回购物车列表界面
			return "cart";
		}
		
		//将数据返回至前台界面呢
		request.setAttribute("cartList", list);
		
		//返回购物车列表界面
		return "cart";
	}
	
	/**
	 * 在购物车界面修改购物车的数量
	 * 返回一个letaoresult，是一个json对象，那么不能以.html为后缀，所以又定义了一个以.action为后缀的
	 * @param itemId 商品id
	 * @param num 修改后的数量
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public LetaoResult updateCartList(@PathVariable Long itemId,@PathVariable Integer num,
											HttpServletRequest request,HttpServletResponse response) throws Exception{
		//查看用户是否登录，如果登录就修改redis中的购物车数量
		TbUser user = getTbUserByToken(request);
		if(user != null) {
			String itemJson = jedisClient.hget(CART_REIDS_KEY + ":" + user.getId() + ":BASE", itemId.toString());
			TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
			//修改数量
			item.setNum(num);
			//再写回redis
			jedisClient.hset(CART_REIDS_KEY + ":" + user.getId() + ":BASE", itemId.toString(), JsonUtils.objectToJson(item));
			return LetaoResult.ok();
		}
		
		//如果未登录
		//从cookie中获取商品列表
		List<TbItem> list = getCartList(request);
		//遍历列表
		for (TbItem item : list) {
			if(item.getId().longValue() == itemId.longValue()) {
				//如果找到对应的商品，则修改数量
				item.setNum(num);
				//修改后跳出循环
				break;
			}
		}
		//将商品列表重新写入cookie中
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(list), CART_EXPIER, true);
		return LetaoResult.ok();
	}
	
	/**
	 * 删除购物车中的某一个商品
	 * @param itemId 商品id
	 * @param request
	 * @param response
	 * @return 返回重定向到/cart/cart.html 展示购物车列表
 	 * @throws Exception
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartList(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//查看用户是否登录，如果登录就修改redis中的购物车数量
		TbUser user = getTbUserByToken(request);
		if(user != null) {
			jedisClient.hdel(CART_REIDS_KEY + ":" + user.getId() + ":BASE", itemId.toString());
			return "redirect:/cart/cart.html";
		}
		
		//用户未登录
		//从cookie中获取购物车列表
		List<TbItem> list = getCartList(request);
		for (TbItem item : list) {
			if(item.getId().longValue() == itemId.longValue()) {
				//删除
				list.remove(item);
				//删完赶紧跑，以免循环出错
				break;
			}
		}
		//写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(list), CART_EXPIER, true);
		return "redirect:/cart/cart.html";
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
	 * 查看用户是否登录
	 * @return
	 */
	private TbUser getTbUserByToken(HttpServletRequest request) {
		//判断用户是否登录，如果登录了，就将商品写入redis中，没登录写入cookie中，在查看购物车时做cookie和redis的同步
		String token = CookieUtils.getCookieValue(request, TOKEN_KEY);
		if(StringUtils.isBlank(token)) {
			//如果token为空，则用户没登录
			return null;
		}
		
		//token不为空，但是需要判断登录状态
		LetaoResult letaoResult = userService.getUserByToken(token);
		if(letaoResult.getStatus() != 200) {
			//登录过期，返回空
			return null;
		}
		
		//用户登录
		TbUser user = (TbUser) letaoResult.getData();
		return user;
	}
}
