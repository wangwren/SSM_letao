package com.wangwren.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.common.pojo.ItemCatResult;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.service.ItemCatService;

/**
 * 商品类目controller
 * @author wwr
 *
 */
@RequestMapping("/item/cat")
@Controller
public class ItemCatController {

	/**
	 * 记得加上服务消费
	 */
	@Autowired
	private ItemCatService itemCatService;
	
	/**
	 * 根据父节点查询类目
	 * 
	 * 该url在js文件夹中common.js中，// 初始化选择类目组件 initItemCat : function(data){ 中
	 * 
	 * 由于使用easyUI的tree组件，在每次点开一个tree结点时，就会根据这个结点的id去查询其下的子节点，即会有参数传来
	 * 如果是第一次访问，即将参数设为默认值0，即查询所有父节点
	 * 
	 * @RequestParam(name="id",defaultValue="0")记得加上默认值，否则第一次访问没有数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<EasyUIDataTree> getAllItemCat(@RequestParam(name="id",defaultValue="0")Long parentId) throws Exception{
		
		List<EasyUIDataTree> dataTrees = itemCatService.getAllItemCat(parentId);
		
		return dataTrees;
	}
	
	
	/**
	 * 前台所有分类
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/all",produces=MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
	@ResponseBody
	public String getItemCatAll(String callback) throws Exception{
		
		ItemCatResult result = itemCatService.getItemCatAll();
		//判断是否是jsonp调用
		if (StringUtils.isBlank(callback)) {
			return JsonUtils.objectToJson(result);
		}
		return callback + "(" + JsonUtils.objectToJson(result) + ");";
	}
} 
