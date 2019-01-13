package com.wangwren.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.common.pojo.LetaoResult;
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
		
		//删除，可以删除多个，使用in
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(idList);
		tbContentMapper.deleteByExample(example);
		return LetaoResult.ok();
	}

	/**
	 * 根据父类目id查询内容信息
	 */
	@Override
	public List<TbContent> getContentListByCid(Long categoryId) {

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		return list;
	}

}
