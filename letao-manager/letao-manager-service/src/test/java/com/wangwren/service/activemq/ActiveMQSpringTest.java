package com.wangwren.service.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * 
 * @author wwr
 *
 */
public class ActiveMQSpringTest {

	/**
	 * activeMQ与spring整合后，发布消息
	 * @throws Exception
	 */
	@Test
	public void testProducerSpring() throws Exception{
		
		//加载spring配置文件
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		//获取JMSTemplate对象来发送消息
		JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
		//获取Destination目的地
		Destination destination = (Destination) applicationContext.getBean("test-queue");
		
		//使用JMSTemplate来发送消息
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage("hello active spring456");
				return textMessage;
			}
		});
	}
	
}
