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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.IDUtils;
import com.wangwren.mapper.TbItemDescMapper;
import com.wangwren.mapper.TbItemMapper;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbItemDesc;
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
	
	@Override
	public TbItem findItemById(long itemId) {
		
		TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
		
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
	 * 修改指定商品信息
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
		
		return LetaoResult.ok();
	}

	/**
	 * 删除指定id的商品
	 */
	@Override
	public LetaoResult deleteItemById(String ids) {

		String[] split = ids.split(",");
		
		List<Long> idList = new ArrayList<>();
		for(String id : split) {
			idList.add(Long.parseLong(id));
		}
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(idList);
		tbItemMapper.deleteByExample(example);
		
		//发送消息
		sendMessage(ids, itemDeleteTopic);
		
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
