package com.wangwren.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	
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
	 * 添加商品
	 * @param item 商品的相关信息
	 * @param desc 商品描述，商品描述是另一个表
	 * @return
	 */
	@Override
	public LetaoResult addItem(TbItem item, String desc) {
		
		//生成item的id
		Long id = IDUtils.genItemId();
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
		
		return LetaoResult.ok();
	}

}
