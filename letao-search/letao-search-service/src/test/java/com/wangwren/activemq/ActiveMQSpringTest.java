package com.wangwren.activemq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 测试activemq与spring整合后的消费者
 * @author wwr
 *
 */
public class ActiveMQSpringTest {
	
	/**
	 * 测试与spring整合后
	 * @throws Exception
	 */
	@Test
	public void testConsumerSpring() throws Exception{
		//加载spring
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		
		//等待
		System.in.read();
	}
}
