package com.wangwren.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.mapper.TbItemCatMapper;
import com.wangwren.pojo.TbItemCat;
import com.wangwren.pojo.TbItemCatExample;
import com.wangwren.pojo.TbItemCatExample.Criteria;
import com.wangwren.service.ItemCatService;

/**
 * 商品类目service
 * @author wwr
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 根据父节点id查询商品类目，商品类目是一个集合
	 */
	@Override
	public List<EasyUIDataTree> getAllItemCat(Long parentId) {
		
		//根据父节点id查询类目
		TbItemCatExample example = new TbItemCatExample();
		
		//获取条件对象
		Criteria criteria = example.createCriteria();
		//添加查询条件
		criteria.andParentIdEqualTo(parentId);
		
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		
		//将查询到的结果封装到EasyUIDataTree中
		List<EasyUIDataTree> dataTrees = new ArrayList<>();
		for(TbItemCat itemCat : list) {
			EasyUIDataTree dataTree = new EasyUIDataTree();
			//将类目的id给对应的结点，不要设父节点
			dataTree.setId(itemCat.getId());
			dataTree.setText(itemCat.getName());
			dataTree.setState(itemCat.getIsParent() ? "closed" : "open");
			
			dataTrees.add(dataTree);
		}
		
		return dataTrees;
	}

}
