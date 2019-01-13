package com.wangwren.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.content.service.ContentService;
import com.wangwren.pojo.TbContent;

/**
 * 内容管理
 * @author wwr
 *
 */
@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	/**
	 * 查询对应的categoryId下的内容
	 * @param categoryId
	 * @param page 第几页
	 * @param rows 一页多少条
	 * @return
	 */
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getAllContent(Long categoryId,int page,int rows) throws Exception{
		EasyUIDataGridResult result = contentService.getAllContent(categoryId, page, rows);
		return result;
	}
	
	/**
	 * 添加内容
	 * @param content
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/content/save")
	@ResponseBody
	public LetaoResult addContent(TbContent content) throws Exception{
		LetaoResult result = contentService.addContent(content);
		return result;
	}
	
	/**
	 * 修改内容
	 * @param content
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public LetaoResult updateContent(TbContent content) throws Exception{
		LetaoResult result = contentService.updateContent(content);
		return result;
	}
	
	/**
	 * 删除内容
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/content/delete")
	@ResponseBody
	public LetaoResult deleteContentByIds(String ids) throws Exception{
		LetaoResult result = contentService.deleteContent(ids);
		return result;
	}
}
