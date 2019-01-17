package com.wangwren.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.wangwren.common.pojo.SearchItem;
import com.wangwren.search.mapper.SearchItemMapper;
/**
 * 添加商品的消息监听
 * 
 * 根据接收到的消息(商品id)，去查询数据库，之后同步索引库
 * @author wwr
 *
 */
public class ItemAddAndUpdateMessageListener implements MessageListener {

	@Autowired
	private SearchItemMapper searchItemMapper;
	
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		try {
			//接收消息
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			//获取id
			Long id = Long.parseLong(text);
			//等待一会再查，添加商品时数据库事务还未提交而消息先到了
			Thread.sleep(1000);
			//查询商品
			SearchItem searchItem = searchItemMapper.getSearchItemById(id);
			//创建文档对象
			SolrInputDocument document = new SolrInputDocument();
			//添加域
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCat_name());
			
			//添加至索引库
			solrServer.add(document);
			//提交
			solrServer.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
