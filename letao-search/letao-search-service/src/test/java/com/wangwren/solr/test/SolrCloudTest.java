package com.wangwren.solr.test;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * solr集群测试
 * @author wwr
 *
 */
public class SolrCloudTest {

	/**
	 * solr集群版测试
	 * 与单机版相比，就前两步不一样
	 * //创建CloudSolrServer对象，构造方法参数中需要指定zookeeper地址列表
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.128:2182,192.168.25.128:2183,192.168.25.128:2184");
		//指定默认的collection
		cloudSolrServer.setDefaultCollection("collection2");
	 * @throws Exception
	 */
	@Test
	public void testSolrCloud() throws Exception{
		//创建CloudSolrServer对象，构造方法参数中需要指定zookeeper地址列表
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.128:2182,192.168.25.128:2183,192.168.25.128:2184");
		//指定默认的collection
		cloudSolrServer.setDefaultCollection("collection2");
		//剩下的就和单机版的solr相同了
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "test001");
		document.addField("item_title", "测试数据");
		document.addField("item_price", 200);
		
		cloudSolrServer.add(document);
		
		cloudSolrServer.commit();
	}
}
