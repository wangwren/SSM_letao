package com.wangwren.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wangwren.content.jedis.JedisClient;

/**
 * 整个spring
 * @author wwr
 *
 */
public class JedisSpringTest {

	@Test
	public void testJedisSpring() throws Exception{
		//加载spring配置文件
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//获取bean，即JedisClient，获取接口就行了
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		//操作redis数据库
		jedisClient.set("jedisSpring", "hello");
		System.out.println(jedisClient.get("jedisSpring"));
	}
}
