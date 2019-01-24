package com.wangwren.order.jedis;
/**
 * 操作redis数据库的java客户端通用接口(策略模式)
 * @author wwr
 *
 */
public interface JedisClient {

	String set(String key, String value);
	String get(String key);
	Long del(String key);
	Long del(String... keys);
	Boolean exists(String key);
	Long expire(String key, int seconds);
	Long ttl(String key);
	Long incr(String key);
	Long hset(String key, String field, String value);
	String hget(String key, String field);
	Long hdel(String key, String... field);
}
