package com.wangwren.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.content.jedis.JedisClient;
import com.wangwren.content.service.ContentService;
import com.wangwren.mapper.TbContentMapper;
import com.wangwren.pojo.TbContent;
import com.wangwren.pojo.TbContentExample;
import com.wangwren.pojo.TbContentExample.Criteria;
/**
 * 内容管理
 * @author wwr
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	
	/**
	 * redis缓存接口
	 */
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT}")
	private String INDEX_CONTENT;
	
	/**
	 * 查询对应的categoryId下的内容
	 * @param categoryId
	 * @param page 第几页
	 * @param rows 一页多少条
	 * @return
	 */
	@Override
	public EasyUIDataGridResult getAllContent(Long categoryId, int page, int rows) {
		
		PageHelper.startPage(page, rows);
		
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//查询包含大文本
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		
		return result;
	}

	/**
	 * 添加内容
	 */
	@Override
	public LetaoResult addContent(TbContent content) {

		content.setCreated(new Date());
		content.setUpdated(new Date());
		//添加
		tbContentMapper.insert(content);
		
		//缓存同步
		redisSync(content,null);
		
		return LetaoResult.ok();
	}

	/**
	 * 修改内容
	 */
	@Override
	public LetaoResult updateContent(TbContent content) {

		//先查出修改的内容
		TbContent tbContent = tbContentMapper.selectByPrimaryKey(content.getId());
		tbContent.setCreated(content.getCreated());
		tbContent.setContent(content.getContent());
		tbContent.setPic(content.getPic());
		tbContent.setPic2(content.getPic2());
		tbContent.setSubTitle(content.getSubTitle());
		tbContent.setTitle(content.getTitle());
		tbContent.setTitleDesc(content.getTitleDesc());
		tbContent.setUrl(content.getUrl());
		tbContent.setUpdated(new Date());
		
		//修改
		tbContentMapper.updateByPrimaryKeyWithBLOBs(tbContent);
		
		//缓存同步
		redisSync(tbContent,null);
		
		return LetaoResult.ok();
	}


	/**
	 * 删除内容
	 */
	@Override
	public LetaoResult deleteContent(String ids) {

		String[] split = ids.split(",");
		
		List<Long> idList = new ArrayList<>();
		for(String id : split) {
			idList.add(Long.parseLong(id));
		}
		
		//获取要删除内容信息的父节点，使用其中一个内容信息就行
		TbContent content = tbContentMapper.selectByPrimaryKey(idList.get(0));
		
		//删除，可以删除多个，使用in
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(idList);
		tbContentMapper.deleteByExample(example);
		
		//缓存同步
		redisSync(content, null);
		
		return LetaoResult.ok();
	}

	/**
	 * 根据父类目id查询内容信息
	 * 
	 * 做缓存，先查询缓存，如果缓存中没有，那么再查数据库，再将数据库查到的值添加至缓存中，这样下次再查就可以不走数据库了
	 */
	@Override
	public List<TbContent> getContentListByCid(Long categoryId) {

		//添加缓存，查询缓存时，不能向外抛异常，需要捕获
		//因为即使redis查不到，或者报了异常，也不能影响去数据库中查数据
		try {
			//查询出的是字符串
			String hget = jedisClient.hget(INDEX_CONTENT, categoryId.toString());
			
			//如果hget不为空，那么将hget使用json工具转成list返回
			//isNotBlank(hget)是org.apache.commons.lang3下的工具类中方法
			//该方法表示，如果hget不为空，且不为 " " 就为true。
			//其isNotEmpty(hget)不能判断" "。
			if(StringUtils.isNotBlank(hget)) {
				List<TbContent> list = JsonUtils.jsonToList(hget, TbContent.class);
				return list;
			}
			
			//如果为空，什么都不做，代码继续走，去查数据库
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//缓存没命中查询数据库
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		
		//将从数据库中查询出的结果添加至缓存中，同样不能向外抛出异常
		try {
			//数据库中查出是一个List，redis使用hash类型，将list数据转成字符串，存入redis，通过json工具类
			//INDEX_CONTENT表示key，categoryId表示Field，内容类型，因为每一个Field的值都不同
			//查也是找key下的categoryId来查
			jedisClient.hset(INDEX_CONTENT, categoryId.toString(), JsonUtils.objectToJson(list));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * 缓存同步，如果做了增加、修改、删除，就将categoryId在redis中的缓存删除
	 * @param tbContent
	 */
	private void redisSync(TbContent tbContent,String[] ids) {
		try {
			//如果是增加、修改、删除
			if(tbContent != null) {
				jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
			}else {
				//如果是删除，这行代码没用了
				jedisClient.hdel(INDEX_CONTENT, ids);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
