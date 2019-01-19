package com.wangwren.search.mapper;

import java.util.List;

import com.wangwren.common.pojo.SearchItem;

/**
 * 搜索商品对应的mapper
 * @author wwr
 *
 */
public interface SearchItemMapper {

	/**
	 * 搜索全部searchItem，自己写mapper
	 * @return
	 */
	public List<SearchItem> getAllSearchItem();
	
	/**
	 * 根据商品id查询商品，用于监听到添加商品时，去查数据库同步索引库
	 * @param itemId
	 * @return
	 */
	public SearchItem getSearchItemById(Long itemId);
	
}
