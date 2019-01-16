package com.wangwren.search.service;

import java.util.List;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.pojo.SearchItem;

/**
 * 商品搜索相关
 * @author wwr
 *
 */
public interface SearchItemService {

	/**
	 * 查询出所有商品，并导入至索引库中
	 * @return
	 */
	public LetaoResult importSolrIndex();
}
