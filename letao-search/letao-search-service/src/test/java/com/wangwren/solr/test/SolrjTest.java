package com.wangwren.solr.test;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrjTest {

	/**
	 * 向solr服务器中添加文档
	 * @throws Exception
	 */
	@Test
	public void addSearchItem() throws Exception{
		//连接solr服务器，默认连接的是solr的collection1实例
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		//创建solrInputDocument文档对象
		SolrInputDocument document = new SolrInputDocument();
		//向文档中添加域
		document.addField("id", "123");
		document.addField("item_title", "啦啦啦啦");
		document.addField("item_price", 10000);
		//将文档添加到solr服务器中
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	
	/**
	 * 根据指定id，删除索引库中的文档
	 * @throws Exception
	 */
	@Test
	public void deleteSearchItem() throws Exception{
		//连接solr服务器，默认连接的是solr的collection1实例
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		solrServer.deleteById("123");
		solrServer.commit();
	}
	
	/**
	 * 查询测试
	 * @throws Exception
	 */
	//@Test
	/*public void getSearchItem() throws Exception{
		//连接solr服务器
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr");
		//SolrQuery查询对象
		SolrQuery solrQuery = new SolrQuery();
		//设置查询条件
		solrQuery.setQuery("手机");
		//设置默认搜索域
		solrQuery.set("df", "item_keywords");
		//分页
		solrQuery.setStart(0);
		solrQuery.setRows(10);
		
		//高亮
		solrQuery.setHighlight(true);
		//高亮显示的域
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");
		
		//执行查询
		QueryResponse response = solrServer.query(solrQuery);
		//获取查询结果集
		SolrDocumentList solrDocumentList = response.getResults();
		
		//总记录数
		System.out.println("总记录数:" + solrDocumentList.getNumFound());
		
		//遍历结果集
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			//获取高亮
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = null;
			if(list != null && list.size() > 0) {
				title = list.get(0);
			}else {
				title = (String) solrDocument.get("item_title");
			}
			System.out.println(title);
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
			System.out.println("=====================================");
		}
	}*/
}
