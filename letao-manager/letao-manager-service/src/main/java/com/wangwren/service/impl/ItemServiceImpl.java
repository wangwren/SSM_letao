package com.wangwren.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.IDUtils;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.manager.jedis.JedisClient;
import com.wangwren.mapper.TbItemDescMapper;
import com.wangwren.mapper.TbItemMapper;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbItemDesc;
import com.wangwren.pojo.TbItemDescExample;
import com.wangwren.pojo.TbItemExample;
import com.wangwren.pojo.TbItemExample.Criteria;
import com.wangwren.service.ItemService;
/**
 * 商品的service
 * @author wwr
 *
 */
@Service
public class ItemServiceImpl implements ItemService {

	/**
	 * 商品的dao
	 */
	@Autowired
	private TbItemMapper tbItemMapper;
	
	/**
	 * 商品描述dao
	 */
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	
	/**
	 * redis缓存接口
	 */
	@Autowired
	private JedisClient jedisClient;
	
	/**
	 * 发送消息对象
	 */
	@Autowired
	private JmsTemplate jmsTemplate;
	
	/**
	 * activeMQ发送消息的目的地
	 * @Resource 注解是java提供的，它可以根据id来注入，也可以通过对应的类来注入
	 * @Autowired 注解是spring提供的，根据类来注入
	 * 
	 * 添加商品的目的地
	 */
	@Resource(name="itemAddTopic")
	private Destination itemAddTopic;
	
	/**
	 * 修改商品目的地
	 */
	@Resource(name="itemUpdateTopic")
	private Destination itemUpdateTopic;
	
	/**
	 * 删除商品目的地
	 */
	@Resource(name="itemDeleteTopic")
	private Destination itemDeleteTopic;
	
	/**
	 * 商品详情在redis中的key
	 */
	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	
	/**
	 * 根据商品id查询商品的详细信息
	 * 
	 * 为该方法添加缓存并设置缓存的有效时间为一天
	 * 
	 * 对于点击过查看商品详情的商品，先去缓存中去查是否存在，如果不存在就去数据库中查之后添加至缓存中。
	 * 如果缓存中存在就从缓存中取。
	 * 
	 * 设置有效时间为一天，这样对于热门商品，缓存中就会一直有数据，对于点击次数少的商品，有效时间到了，缓存中就没有该商品的数据了
	 */
	@Override
	public TbItem findItemById(long itemId) {
		//向缓存中搜索商品信息
		try {
			String jsonString = jedisClient.get(ITEM_INFO + ":" + itemId + ":BASE");
			if(StringUtils.isNotBlank(jsonString)) {
				TbItem tbItem = JsonUtils.jsonToPojo(jsonString, TbItem.class);
				//设置该商品在redis的有效时间，有效时间为一天
				jedisClient.expire(ITEM_INFO + ":" + itemId + ":BASE", 86400);
				return tbItem;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//查询数据库
		TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
		
		//从数据库中查询出的信息添加至缓存
		try {
			jedisClient.set(ITEM_INFO + ":" + itemId + ":BASE", JsonUtils.objectToJson(item));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}

	/**
	 * 使用mybatis的分页插件PageHelper实现商品分页查询
	 */
	@Override
	public EasyUIDataGridResult getAllItem(int pageNum,int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		
		TbItemExample example = new TbItemExample();
		List<TbItem> list = tbItemMapper.selectByExample(example);
		
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		EasyUIDataGridResult dataGridResult = new EasyUIDataGridResult();
		
		dataGridResult.setTotal(pageInfo.getTotal());
		dataGridResult.setRows(list);
		
		return dataGridResult;
	}

	/**
	 * 添加商品，添加后使用activeMQ同步solr索引库
	 * @param item 商品的相关信息
	 * @param desc 商品描述，商品描述是另一个表
	 * @return
	 */
	@Override
	public LetaoResult addItem(TbItem item, String desc) {
		
		//生成item的id，发送消息的内部类中需要用到，所以需要final修饰
		final Long id = IDUtils.genItemId();
		//补全item，item的id和状态，创建时间，更新时间
		item.setId(id);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		
		//插入商品信息
		tbItemMapper.insert(item);
		
		//商品信息
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(id);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		
		//插入商品描述
		tbItemDescMapper.insert(itemDesc);
		
		//发送消息，发送给添加商品的id，之后在search系统中，消费者通过id去查数据库就行了
		//方法执行完，事务才会提交，所以在消费者那需要等一会，很有可能事务还没提交，消息已经到了
		sendMessage(id.toString(),itemAddTopic);
		
		//返回自己封装的一个pojo，包含status状态码，供前台判断使用
		return LetaoResult.ok();
	}

	

	/**
	 * 根据商品id查询商品描述信息
	 */
	@Override
	public LetaoResult getItemDescById(Long id) {

		TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(id);
		
		return LetaoResult.ok(itemDesc);
	}
	
	/**
	 * 根据商品id获取商品描述信息，用于前台界面查询商品
	 * 
	 * 添加缓存
	 * @param id
	 * @return 返回商品的描述信息
	 */
	@Override
	public TbItemDesc getItemDescById2Index(Long id) {

		//向缓存中搜索商品信息
		try {
			String jsonString = jedisClient.get(ITEM_INFO + ":" + id + ":DESC");
			if(StringUtils.isNotBlank(jsonString)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(jsonString, TbItemDesc.class);
				//设置该商品在redis的有效时间，有效时间为一天
				jedisClient.expire(ITEM_INFO + ":" + id + ":DESC", 86400);
				return tbItemDesc;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//查询数据库
		TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(id);
		
		//从数据库中查询出的信息添加至缓存
		try {
			jedisClient.set(ITEM_INFO + ":" + id + ":DESC", JsonUtils.objectToJson(itemDesc));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}


	/**
	 * 修改指定商品信息
	 * 
	 * 缓存同步，删除缓存中的数据
	 */
	@Override
	public LetaoResult updateItem(TbItem item, String desc) {

		//补全商品信息
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		
		//修改商品信息
		tbItemMapper.updateByPrimaryKey(item);
		
		//修改商品描述信息
		TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		tbItemDescMapper.updateByPrimaryKeySelective(itemDesc);
		
		//发送消息
		sendMessage(item.getId().toString(),itemUpdateTopic);
		
		//缓存同步
		jedisClient.del(ITEM_INFO + ":" + item.getId() + ":BASE");
		jedisClient.del(ITEM_INFO + ":" + item.getId() + ":DESC");
		
		return LetaoResult.ok();
	}

	/**
	 * 删除指定id的商品，同时删除id对应的商品详细信息
	 */
	@Override
	public LetaoResult deleteItemById(String ids) {

		String[] split = ids.split(",");
		
		List<Long> idList = new ArrayList<>();
		for(String id : split) {
			idList.add(Long.parseLong(id));
		}
		
		//删除商品
		TbItemExample itemExample = new TbItemExample();
		Criteria criteria = itemExample.createCriteria();
		criteria.andIdIn(idList);
		tbItemMapper.deleteByExample(itemExample);
		
		//删除商品详细信息
		TbItemDescExample itemDescExample = new TbItemDescExample();
		com.wangwren.pojo.TbItemDescExample.Criteria criteria2 = itemDescExample.createCriteria();
		criteria2.andItemIdIn(idList);
		tbItemDescMapper.deleteByExample(itemDescExample);
		
		//发送消息
		sendMessage(ids, itemDeleteTopic);
		
		//缓存同步
		for(String id : split) {
			jedisClient.del(ITEM_INFO + ":" + id + ":BASE");
			jedisClient.del(ITEM_INFO + ":" + id + ":DESC");
		}
		
		return LetaoResult.ok();
	}
	
	/**
	 * 发送消息的通用方法
	 * @param id
	 * @param destination
	 */
	private void sendMessage(final String id,Destination destination) {
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(id);
				return textMessage;
			}
		});
	}

}
