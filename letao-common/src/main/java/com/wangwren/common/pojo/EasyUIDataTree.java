package com.wangwren.common.pojo;

import java.io.Serializable;

/**
 * 用于前端商品类目的返回信息
 * easyUI要求的数据格式：
 * 
 * 初始化tree时只需要把第一级节点展示，子节点异步加载。
	long id（父节点id）
	返回值：json。数据格式
	[{    
	    "id": 1,    
	    "text": "Node 1",    
	    "state": "closed"
	},{    
	    "id": 2,    
	    "text": "Node 2",    
	    "state": "closed"   
	}] 
	state：如果节点下有子节点“closed”，如果没有子节点“open”

 * @author wwr
 *
 */
public class EasyUIDataTree implements Serializable {

	/**
	 * 父节点ID
	 */
	private Long id;
	
	/**
	 * 类目名称
	 */
	private String text;
	
	/**
	 * 如果节点下有子节点“closed”，如果没有子节点“open”
	 */
	private String state;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}
