package com.wangwren.common.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 首页显示分类通用pojo
 * 
 * 封装成json
 * 格式：
 * 	"data" : [
		{
			"u" : "/products/1.html",
			"n" : "<a href='/products/1.html'>图书、音像、电子书刊</a>",
			"i" : [
				{
					"u" : "/products/2.html",
					"n" : "电子书刊",
					"i" : [
						"/products/3.html|电子书",
						"/products/4.html|网络原创",
						"/products/5.html|数字杂志",
						"/products/6.html|多媒体图书"
					]
				}, 
 * @author wwr
 *
 */
public class ItemCatNode implements Serializable {
	
	@JsonProperty("u")
	private String url;
	
	@JsonProperty("n")
	private String name;
	
	@JsonProperty("i")
	private List<?> items;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<?> getItems() {
		return items;
	}

	public void setItems(List<?> items) {
		this.items = items;
	}

}
