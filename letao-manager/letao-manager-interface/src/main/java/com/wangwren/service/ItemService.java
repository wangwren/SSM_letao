package com.wangwren.service;

import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.pojo.TbItem;
/**
 * 商品的方法定义
 * @author wwr
 *
 */
public interface ItemService {

	/**
	 * 根据商品id查询商品
	 * @param itemId
	 * @return
	 */
	public TbItem findItemById(long itemId);
	
	/**
	 * 获取所有商品
	 * @return
	 */
	public EasyUIDataGridResult getAllItem(int pageNum,int pageSize);
}
