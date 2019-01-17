package com.wangwren.common.pojo;

import java.io.Serializable;
/**
 * 用于搜索商品和导入索引库时的pojo
 * 字段对应的是solr索引库中配置的字段
 * @author wwr
 *
 */
public class SearchItem implements Serializable {

	/**
	 * 商品id
	 */
	private String id;
	/**
	 * 商品标题
	 */
	private String title;
	/**
	 * 商品卖点
	 */
	private String sell_point;
	/**
	 * 商品价格
	 */
	private Long price;
	/**
	 * 商品图片
	 */
	private String image;
	/**
	 * 商品类型
	 */
	private String cat_name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSell_point() {
		return sell_point;
	}
	public void setSell_point(String sell_point) {
		this.sell_point = sell_point;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getCat_name() {
		return cat_name;
	}
	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}
}
