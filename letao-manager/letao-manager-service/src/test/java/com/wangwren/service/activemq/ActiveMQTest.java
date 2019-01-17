package com.wangwren.service.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

/**
 * 消息队列的测试
 * @author wwr
 *
 */
public class ActiveMQTest {

	/**
	 * 测试生产者消息队列，Queue的方式，即点到点(一个生产者对应一个消费者)
	 * 发送出去的消息持久化
	 * @throws Exception
	 */
	@Test
	public void testQueueProducer() throws Exception{
		//1.创建一个ConnectionFactory工厂，用来连接ActiveMQ服务
		//构造方法中指定ActiveMQ的地址及端口，这个端口与服务端的不一样
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		
		//2.connectionFactory创建一个连接
		Connection connection = connectionFactory.createConnection();
		//3.开启连接
		connection.start();
		
		//4.使用connection连接对象创建一个session会话对象
		//第一个参数:表示是否开启事务，一般不开启。保证数据一致，可以使用消息队列实现
		//如果第一个参数为true，那么自动忽略第二个参数；
		//如果第一个参数为false，不开启事务。那么第二个参数表示消息的应答模式，一般设为自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//5.使用session对象创建一个Destination(目的地)对象，有两种形式queue(点到点)和topic(类似广播)
		//此处的目的地在消费方也会使用到，类似生产者生产出一个东西放到指定位置，消费者再去拿
		Queue queue = session.createQueue("test-queue");
		
		//6.使用session对象创建一个生产者producer，并为其指定目的地
		MessageProducer producer = session.createProducer(queue);
		
		//7.创建一个TextMessgae对象，用来放要发送的消息信息
		/*TextMessage textMessage = new ActiveMQTextMessage();
		textMessage.setText("hello active queue");*/
		//也可以使用session来创建
		TextMessage textMessage = session.createTextMessage("hello active queue123");
		
		//8.发送消息
		producer.send(textMessage);
		
		//9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	/**
	 * 测试queue的消费者
	 * @throws Exception
	 */
	@Test
	public void testQueueConsumer() throws Exception{
		//1.创建ConnectionFactory工厂，指定地址
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//2.创建连接对象
		Connection connection = connectionFactory.createConnection();
		//3.启动连接
		connection.start();
		//4.创建session会话对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.创建目的地Destination
		Queue queue = session.createQueue("test-queue");
		//6.创建消费者
		MessageConsumer consumer = session.createConsumer(queue);
		
		//7.接下来就与生产者不同了
		//向consumer对象中设置MessageListener对象，用来接收消息
		consumer.setMessageListener(new MessageListener() {
			/**
			 * 当有生产者生产消息时就会触发这个方法
			 */
			@Override
			public void onMessage(Message message) {
				if(message instanceof TextMessage) {
					//获取消息信息
					TextMessage textMessage = (TextMessage) message;
					try {
						String text = textMessage.getText();
						//打印消息
						System.out.println(text);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		//系统等待接收消息，应该是一个死循环，因为不知道什么时候会再来消息
		/*while(true) {
			Thread.sleep(1000);
		}*/
		//也可以使用另一种方式等待，当键盘输入时就会停止等待
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	/**
	 * 测试Topic生产者
	 * Topic发布的消息不会被持久化到服务器中，但是可以被多个Topic的消费者接收到
	 * 
	 * 与queue的区别就是在创建目的地Detination时，选择topic的方式，其他都一样
	 * @throws Exception
	 */
	@Test
	public void testTopicProducer() throws Exception{
		//1.创建一个ConnectionFactory工厂，用来连接ActiveMQ服务
		//构造方法中指定ActiveMQ的地址及端口，这个端口与服务端的不一样
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		
		//2.connectionFactory创建一个连接
		Connection connection = connectionFactory.createConnection();
		//3.开启连接
		connection.start();
		
		//4.使用connection连接对象创建一个session会话对象
		//第一个参数:表示是否开启事务，一般不开启。保证数据一致，可以使用消息队列实现
		//如果第一个参数为true，那么自动忽略第二个参数；
		//如果第一个参数为false，不开启事务。那么第二个参数表示消息的应答模式，一般设为自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//5.使用session对象创建一个Destination(目的地)对象，有两种形式queue(点到点)和topic(类似广播)
		//此处的目的地在消费方也会使用到，类似生产者生产出一个东西放到指定位置，消费者再去拿
		//Queue queue = session.createQueue("test-queue");
		Topic topic = session.createTopic("test-topic");
		
		//6.使用session对象创建一个生产者producer，并为其指定目的地
		MessageProducer producer = session.createProducer(topic);
		
		//7.创建一个TextMessgae对象，用来放要发送的消息信息
		/*TextMessage textMessage = new ActiveMQTextMessage();
		textMessage.setText("hello active queue");*/
		//也可以使用session来创建
		TextMessage textMessage = session.createTextMessage("hello active topic");
		
		//8.发送消息
		producer.send(textMessage);
		
		//9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	/**
	 * 测试topic的消费者
	 * @throws Exception
	 */
	@Test
	public void testTopicConsumer() throws Exception{
		//1.创建ConnectionFactory工厂，指定地址
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//2.创建连接对象
		Connection connection = connectionFactory.createConnection();
		//3.启动连接
		connection.start();
		//4.创建session会话对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.创建目的地Destination
		//Queue queue = session.createQueue("test-queue");
		Topic topic = session.createTopic("test-topic");
		//6.创建消费者
		MessageConsumer consumer = session.createConsumer(topic);
		
		//7.接下来就与生产者不同了
		//向consumer对象中设置MessageListener对象，用来接收消息
		consumer.setMessageListener(new MessageListener() {
			/**
			 * 当有生产者生产消息时就会触发这个方法
			 */
			@Override
			public void onMessage(Message message) {
				if(message instanceof TextMessage) {
					//获取消息信息
					TextMessage textMessage = (TextMessage) message;
					try {
						String text = textMessage.getText();
						//打印消息
						System.out.println(text);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		//系统等待接收消息，应该是一个死循环，因为不知道什么时候会再来消息
		/*while(true) {
			Thread.sleep(1000);
		}*/
		//也可以使用另一种方式等待，当键盘输入时就会停止等待
		System.out.println("topic消费者3...");
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
}
