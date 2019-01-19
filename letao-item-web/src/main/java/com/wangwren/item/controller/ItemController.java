package com.wangwren.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.item.pojo.Item;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbItemDesc;
import com.wangwren.service.ItemCatService;
import com.wangwren.service.ItemService;

/**
 * 商品详情页面
 * @author wwr
 *
 */
@Controller
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private ItemCatService itemCatService;
	
	/**
	 * 根据商品id查询某个商品的详情
	 * 返回商品详情页面
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{itemId}")
	public String getItemById(@PathVariable Long itemId,Model model) throws Exception{
		//查询商品详情
		TbItem tbItem = itemService.findItemById(itemId);
		Item item = new Item(tbItem);
		
		//查询商品对应的所有类型
		String cnames = itemCatService.getItemCatByCid(tbItem.getCid());
		String[] split = cnames.split(",");
		//System.out.println(cnames);
		
		//查询商品描述信息
		TbItemDesc itemDesc = itemService.getItemDescById2Index(itemId);
		
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		model.addAttribute("cnames", split);
		
		//返回详情页面
		return "item";
	}
}
