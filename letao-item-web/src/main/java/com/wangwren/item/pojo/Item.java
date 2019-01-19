package com.wangwren.item.pojo;

import com.wangwren.pojo.TbItem;

/**
 * 商品详情页面的pojo
 * @author wwr
 *
 */
public class Item extends TbItem {

	/**
	 * 初始化
	 * @param item
	 */
	public Item(TbItem tbItem) {
		this.setId(tbItem.getId());
		this.setTitle(tbItem.getTitle());
		this.setSellPoint(tbItem.getSellPoint());
		this.setPrice(tbItem.getPrice());
		this.setNum(tbItem.getNum());
		this.setBarcode(tbItem.getBarcode());
		this.setImage(tbItem.getImage());
		this.setCid(tbItem.getCid());
		this.setStatus(tbItem.getStatus());
		this.setCreated(tbItem.getCreated());
		this.setUpdated(tbItem.getUpdated());
	}
	
	/**
	 * 详情页面的图片
	 * @return
	 */
	public String[] getImages() {
		if (this.getImage()!=null && !"".equals(this.getImage())) {
			String image2 = this.getImage();
			String[] strings = image2.split(",");
			return strings;
		}
		return null;
	}
	
}
