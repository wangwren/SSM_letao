package com.wangwren.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wangwren.common.pojo.SearchItem;
import com.wangwren.common.pojo.SearchResult;

/**
 * 商品搜索的dao
 * @author wwr
 *
 */
@Repository
public class SearchDao {

	@Autowired
	private SolrServer solrServer;
	
	/**
	 * 全文检索查询，给定好查询条件
	 * @param 查询条件，由service封装好，这里直接查出结果返回
	 * @return
	 * @throws Exception
	 */
	public SearchResult search(SolrQuery solrQuery) throws Exception{
		//搜索返回后的pojo
		SearchResult result = new SearchResult();
		//执行查询
		QueryResponse response = solrServer.query(solrQuery);
		//获取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		//获取总记录数
		result.setRecordCount(solrDocumentList.getNumFound());
		//遍历结果
		List<SearchItem> list = new ArrayList<>();
		for (SolrDocument solrDocument : solrDocumentList) {
			//封装进SearchItem
			SearchItem item = new SearchItem();
			item.setCat_name((String) solrDocument.get("item_category_name"));
			item.setId((String) solrDocument.get("id"));
			//取第一张图片
			String image = (String) solrDocument.get("item_image");
			if(StringUtils.isNotBlank(image)) {
				image = image.split(",")[0];
			}
			item.setImage(image);
			item.setPrice((Long) solrDocument.get("item_price"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			
			//设置高亮
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> highlight = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = null;
			if(highlight != null && highlight.size() > 0) {
				title = highlight.get(0);
			}else {
				title = (String) solrDocument.get("item_title");
			}
			
			item.setTitle(title);
			
			list.add(item);
		}
		result.setSearchItems(list);
		return result;
	}
}
