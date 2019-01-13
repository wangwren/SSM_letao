package com.wangwren.content.service;
/**
 * 内容管理
 * @author wwr
 *
 */

import java.util.List;

import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.pojo.TbContent;

public interface ContentService {

	/**
	 * 查询对应的categoryId下的内容
	 * @param categoryId
	 * @param page 第几页
	 * @param rows 一页多少条
	 * @return
	 */
	public EasyUIDataGridResult getAllContent(Long categoryId,int page,int rows);
	
	/**
	 * 添加内容
	 * @param content
	 * @return
	 */
	public LetaoResult addContent(TbContent content);
	
	/**
	 * 修改内容
	 * @param content
	 * @return
	 */
	public LetaoResult updateContent(TbContent content);
	
	/**
	 * 删除内容
	 * @param ids
	 * @return
	 */
	public LetaoResult deleteContent(String ids);
	
	/**
	 * 根据父类目id查询内容信息
	 * @param categoryId
	 * @return
	 */
	public List<TbContent> getContentListByCid(Long categoryId);
}
