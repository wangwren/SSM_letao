package com.wangwren.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.pojo.TbItem;
import com.wangwren.service.ItemService;
/**
 * 商品的controller
 * @author wwr
 *
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	/**
	 * 根据商品id查询对应的商品
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem findItemById(@PathVariable long itemId) throws Exception{
		TbItem item = itemService.findItemById(itemId);
		
		return item;
	}
	
	/**
	 * 获取所有商品
	 * item-list.jsp中请求数据的url:'/item/list'
	 */
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult itemList(int page,int rows) throws Exception{
		
		EasyUIDataGridResult result = itemService.getAllItem(page, rows);
		
		return result;
	}
}
