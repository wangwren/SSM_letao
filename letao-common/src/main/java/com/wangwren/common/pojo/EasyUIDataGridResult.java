package com.wangwren.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 前台用于easyUI查询全部商品的数据
 * 由于easyUI对数据格式有专门的要求，所以封装该对象，最后以json格式返回给前台
 * 
 * Easyui中datagrid控件要求的数据格式为：
 * {total:”2”,rows:[{“id”:”1”,”name”:”张三”},{“id”:”2”,”name”:”李四”}]}
 * 必须要有total:表示查询出的数量
 * rows是一个List，其中有每个数据
 * @author wwr
 *
 */
public class EasyUIDataGridResult implements Serializable {

	private Long total;
	
	/**
	 * 泛型不确定，设成通用的类
	 */
	private List<?> rows;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}
	
}
