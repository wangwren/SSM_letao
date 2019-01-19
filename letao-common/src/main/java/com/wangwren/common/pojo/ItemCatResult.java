package com.wangwren.common.pojo;

import java.io.Serializable;

/**
 * 最终封装好的首页分类，放在该pojo中
 * @author wwr
 *
 */
public class ItemCatResult implements Serializable {

	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
