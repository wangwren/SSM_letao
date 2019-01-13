package com.wangwren.service;

import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
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
	
	/**
	 * 添加商品
	 * @param item 商品的相关信息
	 * @param desc 商品描述，商品描述是另一个表
	 * @return
	 */
	public LetaoResult addItem(TbItem item,String desc);
	
	/**
	 * 根据商品id获取商品描述信息
	 * @param id
	 * @return
	 */
	public LetaoResult getItemDescById(Long id);
	
	/**
	 * 修改商品
	 * @param item
	 * @param desc
	 * @return
	 */
	public LetaoResult updateItem(TbItem item,String desc);
	
	/**
	 * 删除指定id的商品
	 * @param ids
	 * @return
	 */
	public LetaoResult deleteItemById(String ids);
}
