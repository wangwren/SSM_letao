package com.wangwren.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.content.service.ContentCategoryService;

/**
 * 内容分类的controller
 * @author wwr
 *
 */
@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	/**
	 * 获取内容分类
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUIDataTree> getContentCategory(@RequestParam(name="id",defaultValue="0")Long parentId) throws Exception {
		List<EasyUIDataTree> result = contentCategoryService.getContentCategory(parentId);
		return result;
	}
	
	/**
	 * 添加节点
	 * @param parentId
	 * @param name
	 * @return
	 */
	@RequestMapping("/content/category/create")
	@ResponseBody
	public LetaoResult addContentCategory(Long parentId, String name) throws Exception {
		LetaoResult result = contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	
	/**
	 * 内容分类的重命名
	 * @param id
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/content/category/update")
	@ResponseBody
	public LetaoResult updateContentCategory(Long id,String name) throws Exception{
		LetaoResult result = contentCategoryService.updateContentCategory(id, name);
		return result;
	}
	
	/**
	 * 删除指定id的内容分类
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping("/content/category/delete/")
	@ResponseBody
	public LetaoResult deleteContentCategory(Long id) throws Exception{
		LetaoResult result = contentCategoryService.deleteContentCategory(id);
		return result;
	}
}
