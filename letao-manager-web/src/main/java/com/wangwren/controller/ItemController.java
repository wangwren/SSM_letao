package com.wangwren.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
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
	
	/**
	 * 添加商品
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/item/save")
	@ResponseBody
	public LetaoResult addItem(TbItem item,String desc) throws Exception{
		LetaoResult result = itemService.addItem(item, desc);
		return result;
	}
	
	/**
	 * 商品修改界面
	 * @return
	 */
	@RequestMapping("/rest/page/item-edit")
	public String showUpdateItem() {

		return "item-edit";
	}
	
	/**
	 * 根据商品id查询商品描述信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public LetaoResult getItemDescById(@PathVariable Long id) throws Exception {
		LetaoResult result = itemService.getItemDescById(id);
		return result;
	}
	
	/**
	 * 修改商品
	 * @param item
	 * @param desc
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/rest/item/update")
	@ResponseBody
	public LetaoResult updateItem(TbItem item,String desc) throws Exception{
		LetaoResult result = itemService.updateItem(item, desc);
		return result;
	}
	
	/**
	 * 删除指定id的商品
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public LetaoResult deleteItemByIds(String ids) throws Exception{
		LetaoResult result = itemService.deleteItemById(ids);
		return result;
	}
}
