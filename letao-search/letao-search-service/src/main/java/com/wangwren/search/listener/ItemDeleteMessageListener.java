package com.wangwren.search.listener;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 删除商品时的消息监听
 * @author wwr
 *
 */
public class ItemDeleteMessageListener implements MessageListener {

	@Autowired
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		try {
			//接收消息
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			
			String[] ids = text.split(",");
			List<String> idList = new ArrayList<>();
			for (String id : ids) {
				idList.add(id);
			}
			//删除索引库中的数据
			solrServer.deleteById(idList);
			solrServer.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
