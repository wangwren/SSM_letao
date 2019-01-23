package com.wangwren.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.CookieUtils;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.pojo.TbItem;
import com.wangwren.service.ItemService;
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
	
	@Value("${CART_EXPIER}")
	private int CART_EXPIER;
	
	@Autowired
	private ItemService itemService;
	
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
	public String showCartList(HttpServletRequest request) throws Exception{
		//从cookie中取出购物车列表
		List<TbItem> list = getCartList(request);
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
	
}
