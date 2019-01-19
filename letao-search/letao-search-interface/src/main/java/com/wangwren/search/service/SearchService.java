package com.wangwren.search.service;
/**
 * 商品搜索
 * @author wwr
 *
 */

import com.wangwren.common.pojo.SearchResult;

public interface SearchService {

	/**
	 * 搜索
	 * @param queryString 搜索条件
	 * @param page 第几页
	 * @param rows 一页显示多少条数据
	 * @return
	 */
	public SearchResult search(String queryString,Integer page,Integer rows) throws Exception;
	
	/**
	 * 根据商品分类查询商品
	 * @param queryString
	 * @param page
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	public SearchResult searchByCname(String queryString,Integer page,Integer rows) throws Exception;
}
