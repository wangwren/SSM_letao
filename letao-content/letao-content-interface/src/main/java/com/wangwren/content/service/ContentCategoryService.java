package com.wangwren.content.service;
/**
 * 内容分类管理接口
 * @author wwr
 *
 */

import java.util.List;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.common.pojo.LetaoResult;

public interface ContentCategoryService {

	/**
	 * 获取内容分类
	 * 根据父ID查询
	 * @return
	 */
	public List<EasyUIDataTree> getContentCategory(Long parentId);
	
	/**
	 * 添加一个内容分类
	 * @param parentId
	 * @param name
	 * @return
	 */
	public LetaoResult addContentCategory(Long parentId,String name);
	
	/**
	 * 根据内容分类id，修改内容分类的名称
	 * @param id
	 * @param name
	 * @return
	 */
	public LetaoResult updateContentCategory(Long id,String name);
	
	/**
	 * 删除指定id的内容分类
	 * @param id
	 * @return
	 */
	public LetaoResult deleteContentCategory(Long id);
}
