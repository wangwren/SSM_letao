package com.wangwren.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * redis测试
 * @author wwr
 *
 */
public class JedisTest {

	@Test
	public void testJedis() {
		//连接redis服务端
		Jedis jedis = new Jedis("192.168.25.128", 6379);
		//操作数据库
		jedis.set("jedis-key", "hello reids");
		System.out.println(jedis.get("jedis-key"));
		//关闭连接
		jedis.close();
	}
	
	/**
	 * redis连接池
	 */
	@Test
	public void testJedisPool() {
		//创建连接池，单例的
		JedisPool jedisPool = new JedisPool("192.168.25.128", 6379);
		//获取连接
		Jedis jedis = jedisPool.getResource();
		//操作数据库(方法级别，jedis用完就得关闭)
		System.out.println(jedis.get("jedis-key"));
		//关闭数据库，一定要关闭
		jedis.close();
		//系统关闭，关闭连接池
		jedisPool.close();
	}
	
	/**
	 * 连接redis集群
	 * @throws Exception
	 */
	@Test
	public void testJedisCluster() throws Exception{
		Set<HostAndPort> nodes = new HashSet<>();
		//添加节点
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		
		//创建一个JedisCluster对象，构造参数为set类型，集合中的每个元素为HostAndPort类型
		JedisCluster jedisCluster = new JedisCluster(nodes);
		
		//直接使用JedisCluster对象操作redis，自带连接池。jedisCluster可以是单例的
		jedisCluster.set("jedis-cluster", "hello jedis cluster");
		
		System.out.println(jedisCluster.get("jedis-cluster"));
		//系统关闭前关闭JedisCluster
		jedisCluster.close();
	}
}
