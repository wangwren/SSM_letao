package com.wangwren.service;

import java.util.List;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.common.pojo.ItemCatResult;
import com.wangwren.pojo.TbItemCat;

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
	
	/**
	 * 根据类型id查询类型信息
	 * @param cid
	 * @return
	 */
	public String getItemCatByCid(Long cid);
	
	/**
	 * 前台查询出所有的商品分类
	 * @return
	 */
	public ItemCatResult getItemCatAll();
}
