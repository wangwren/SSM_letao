package com.wangwren.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangwren.common.pojo.SearchResult;
import com.wangwren.search.dao.SearchDao;
import com.wangwren.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	/**
	 * 搜索
	 * @param queryString 搜索条件
	 * @param page 第几页
	 * @param rows 一页显示多少条数据
	 * @return
	 */
	@Override
	public SearchResult search(String queryString, Integer page, Integer rows) throws Exception {

		//封装查询对象
		SolrQuery solrQuery = new SolrQuery();
		//查询条件
		solrQuery.setQuery(queryString);
		//设置默认搜索域
		solrQuery.set("df", "item_title");
		//设置分页
		if(page < 1) {
			page = 1;
		}
		solrQuery.setStart((page - 1) * rows);
		solrQuery.setRows(rows);
		//设置高亮
		solrQuery.setHighlight(true);
		//设置要高亮显示的字段
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<font color='red'>");
		solrQuery.setHighlightSimplePost("</font>");
		//查询数据
		SearchResult searchResult = searchDao.search(solrQuery);
		//设置总页数
		int totalPages = (int) (searchResult.getRecordCount() / rows);
		if(searchResult.getRecordCount() / rows != 0) {
			totalPages ++;
		}
		searchResult.setTotalPages(totalPages);
		
		return searchResult;
	}
	
	/**
	 * 根据 商品分类名称查询
	 */
	@Override
	public SearchResult searchByCname(String queryString, Integer page, Integer rows) throws Exception {
		//封装查询对象
		SolrQuery solrQuery = new SolrQuery();
		//查询条件
		solrQuery.setQuery(queryString);
		//设置默认搜索域
		solrQuery.set("df", "item_category_name");
		//设置分页
		if(page < 1) {
			page = 1;
		}
		solrQuery.setStart((page - 1) * rows);
		solrQuery.setRows(rows);
		//设置高亮
		solrQuery.setHighlight(true);
		//设置要高亮显示的字段
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<font color='red'>");
		solrQuery.setHighlightSimplePost("</font>");
		//查询数据
		SearchResult searchResult = searchDao.search(solrQuery);
		//设置总页数
		int totalPages = (int) (searchResult.getRecordCount() / rows);
		if(searchResult.getRecordCount() / rows != 0) {
			totalPages ++;
		}
		searchResult.setTotalPages(totalPages);
		
		return searchResult;
	}
}
