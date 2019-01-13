package com.wangwren.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.content.service.ContentCategoryService;
import com.wangwren.mapper.TbContentCategoryMapper;
import com.wangwren.pojo.TbContentCategory;
import com.wangwren.pojo.TbContentCategoryExample;
import com.wangwren.pojo.TbContentCategoryExample.Criteria;
/**
 * 内容分类管理
 * @author wwr
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	
	/**
	 * 根据父ID查询内容分类
	 */
	@Override
	public List<EasyUIDataTree> getContentCategory(Long parentId) {
		
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		
		criteria.andParentIdEqualTo(parentId);
		
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		
		List<EasyUIDataTree> dataTrees = new ArrayList<>();
		for(TbContentCategory contentCategory : list) {
			EasyUIDataTree dataTree = new EasyUIDataTree();
			dataTree.setId(contentCategory.getId());
			dataTree.setText(contentCategory.getName());
			dataTree.setState(contentCategory.getIsParent() ? "closed" : "open");
			
			dataTrees.add(dataTree);
		}
		
		return dataTrees;
	}

	/**
	 * 添加一条内容分类
	 * 还需要修改mapper.xml中的插入语句，因为id为自增
	 * 插入一个新的内容分类后，需要获取插入后的结点id，使用mysql提供的SELECT LAST_INSERT_ID()
	 * 因为该语句是事务隔离的，所以该多个用户操作时，也不会影响
	 * 
	 * 对于添加后，需要对其父节点进行判断，如果其父节点的isParent为false时，需要改成true，表示该父节点是父节点了
	 */
	@Override
	public LetaoResult addContentCategory(Long parentId, String name) {

		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//该类目是否为父类目，1为true，0为false
		contentCategory.setIsParent(false);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		contentCategory.setSortOrder(1);
		
		tbContentCategoryMapper.insert(contentCategory);
		
		//判断其父节点原先的isParent的值
		TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()) {
			//如果IsParent为false，将其改为true，表示该节点为父节点
			parent.setIsParent(true);
			//修改
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		
		return LetaoResult.ok(contentCategory);
	}

	/**
	 * 根据内容分类id，修改内容分类的名称
	 * @param id
	 * @param name
	 * @return
	 */
	@Override
	public LetaoResult updateContentCategory(Long id, String name) {

		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setId(id);
		contentCategory.setName(name);
		tbContentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		
		return LetaoResult.ok();
	}

	/**
	 * 删除指定分类的id
	 */
	@Override
	public LetaoResult deleteContentCategory(Long id) {

		//先根据id查询出该分类信息
		TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
		if(!contentCategory.getIsParent()) {
			//如果该节点是子节点，获取其父节点信息
			TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(contentCategory.getParentId());
			//删除子节点
			tbContentCategoryMapper.deleteByPrimaryKey(id);
			//判断父节点下是否还有其他子节点，如果没有则将父节点的isParent改为false
			TbContentCategoryExample example = new TbContentCategoryExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(parent.getId());
			List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
			if(list.size() == 0) {
				parent.setIsParent(false);
				//更新父节点
				tbContentCategoryMapper.updateByPrimaryKeySelective(parent);
			}
		}else {
			//如果删除的是父节点，则查询出父节点下的所有子节点，先将子节点删除，再删除父节点
			TbContentCategoryExample example = new TbContentCategoryExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(id);
			List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
			//删除子节点
			for(TbContentCategory category : list) {
				tbContentCategoryMapper.deleteByPrimaryKey(category.getId());
			}
			//最后删除父节点
			tbContentCategoryMapper.deleteByPrimaryKey(id);
		}
		
		return LetaoResult.ok();
	}

}
