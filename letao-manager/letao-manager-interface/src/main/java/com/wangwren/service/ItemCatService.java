package com.wangwren.service;

import java.util.List;

import com.wangwren.common.pojo.EasyUIDataTree;

/**
 * 商品类目的方法定义
 * @author wwr
 *
 */
public interface ItemCatService {

	/**
	 * 根据父节点id查询商品类目
	 */
	public List<EasyUIDataTree> getAllItemCat(Long parentId);
}
