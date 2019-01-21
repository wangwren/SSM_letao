package com.wangwren.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangwren.common.pojo.EasyUIDataTree;
import com.wangwren.common.pojo.ItemCatNode;
import com.wangwren.common.pojo.ItemCatResult;
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
			//将类目的id给对应的结点，不是父节点id
			dataTree.setId(itemCat.getId());
			dataTree.setText(itemCat.getName());
			dataTree.setState(itemCat.getIsParent() ? "closed" : "open");
			
			dataTrees.add(dataTree);
		}
		
		return dataTrees;
	}

	/**
	 * 根据分类id查询商品分类的信息
	 * 将其对应的所以父类都查出来
	 * 用于在商品详情时的分类导航展示
	 */
	@Override
	public String getItemCatByCid(Long cid) {
		
		String cnames = getItemCatByCid(cid,new StringBuilder());
		return cnames;
	}

	/**
	 * 递归的查询每一个商品分类信息，保存至一个StringBuilder中
	 * @param cid
	 * @param cname
	 * @return
	 */
	private String getItemCatByCid(Long cid,StringBuilder cname){
		//查询类型信息
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(cid);
		//拼接类型信息
		cname.append(itemCat.getName());
		cname.append(",");
		if(itemCat.getParentId() != 0) {
			getItemCatByCid(itemCat.getParentId(),cname);
		}
		return cname.toString();
	}

	/**
	 * 前台查询出所有的商品分类
	 * @return
	 */
	@Override
	public ItemCatResult getItemCatAll() {
		ItemCatResult itemCatResult = new ItemCatResult();
		List<?> itemCatAll = getItemCatAll(0L);
		itemCatResult.setData(itemCatAll);
		return itemCatResult;
	}
	
	
	/**
	 * 递归方法，根据parent查询一个树形列表
	 * @param parentId
	 * @return
	 */
	private List<?> getItemCatAll(long parentId) {
		//创建查询条件
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		
		List resultList = new ArrayList<>();
		//循环计数
		int count = 0;
		for (TbItemCat tbItemCat : list) {
			if(count >= 14) {
				break;
			}
			//如果为父节点
			if (tbItemCat.getIsParent()) {
				ItemCatNode node = new ItemCatNode();
				//node.setUrl("http://search.nat300.top/search/item/cat/" + tbItemCat.getName() + ".html");
				node.setUrl("http://localhost:8085/search/item/cat.html?cname=" + tbItemCat.getName());
				//判断是否为第一层节点
				if (parentId == 0) {
					node.setName("<a href='"+node.getUrl()+"'>"+tbItemCat.getName()+"</a>");
					//如果是第一层节点就++，配合前台样式，不要超过14个
					count++;
				} else {
					node.setName(tbItemCat.getName());
				}
				//父节点下还有其他节点，递归
				node.setItems(getItemCatAll(tbItemCat.getId()));
				resultList.add(node);
			} else {
				//如果不是父节点，即json串中的最后item数组
				//String node = "http://search.nat300.top/search/item/cat/"+tbItemCat.getName()+".html|" + tbItemCat.getName();
				String node = "http://localhost:8085/search/item/cat.html?cname="+tbItemCat.getName()+"|" + tbItemCat.getName();
				resultList.add(node);
			}
		}
		return resultList;
	}
}
