package com.wangwren.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwren.common.pojo.EasyUIDataGridResult;
import com.wangwren.mapper.TbItemMapper;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbItemExample;
import com.wangwren.service.ItemService;
/**
 * 商品的service
 * @author wwr
 *
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper tbItemMapper;
	
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

}
