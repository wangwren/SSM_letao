package com.wangwren.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索后返回的pojo
 * @author wwr
 *
 */
public class SearchResult implements Serializable{

	/**
	 * 总页数
	 */
	private Integer totalPages;
	/**
	 * 总记录数
	 */
	private Long recordCount;
	/**
	 * 查询出的商品列表
	 */
	private List<SearchItem> searchItems;
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public List<SearchItem> getSearchItems() {
		return searchItems;
	}
	public void setSearchItems(List<SearchItem> searchItems) {
		this.searchItems = searchItems;
	}
	public Long getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
	}
}
