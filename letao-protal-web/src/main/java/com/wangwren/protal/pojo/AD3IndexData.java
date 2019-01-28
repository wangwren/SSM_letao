package com.wangwren.protal.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 首页楼层中最小单位的数据
 * 
 * {
	"f" : 1,
	"d" : "http://192.168.25.133/group1/M00/00/01/wKgZhVxC2qqAfmZxAAXMpZY29xA051.jpg", 图片
	"e" : "0",  促销标识，0无促销，1限量，2清仓
	"b" : "刘威小狗狗",  商品标题
	"c" : "100",  价格
	"a" : "154788529675342"  商品id
	}
 * @author wwr
 *
 */
public class AD3IndexData {

	/**
	 * 商品标题
	 */
	@JsonProperty("b")
	private String title;
	
	/**
	 * 商品id
	 */
	@JsonProperty("a")
	private String id;
	
	/**
	 * 商品价格
	 */
	@JsonProperty("c")
	private String pic;
	
	/**
	 * 商品图片
	 */
	@JsonProperty("d")
	private String image;
	
	/**
	 * 促销标识：0无促销，1限量，2清仓
	 */
	@JsonProperty("e")
	private String promotion;
	
	/**
	 * 不知道啥鸡吧玩意
	 */
	@JsonProperty("f")
	private Integer express;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public Integer getExpress() {
		return express;
	}

	public void setExpress(Integer express) {
		this.express = express;
	}
	
}
