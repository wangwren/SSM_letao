package com.wangwren.protal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.ItemCatResult;
import com.wangwren.common.utils.JsonUtils;
import com.wangwren.content.service.ContentService;
import com.wangwren.pojo.TbContent;
import com.wangwren.pojo.TbItem;
import com.wangwren.protal.pojo.AD1Node;
import com.wangwren.protal.pojo.AD2Node;
import com.wangwren.protal.pojo.AD3IndexData;
import com.wangwren.service.ItemCatService;
import com.wangwren.service.ItemService;

/**
 * 商城首页
 * @author wwr
 *
 */
@Controller
public class IndexController {

	//引入配置文件中的属性值
	/**
	 * 大轮播图的类目id
	 */
	@Value("${CONTENT_AD_CATEGORY_ID}")
	private Long CONTENT_AD_CATEGORY_ID;
	
	/**
	 * 乐淘快报类目id
	 */
	@Value("${CONTENT_KB_CATEGORY_ID}")
	private Long CONTENT_KB_CATEGORY_ID;
	
	@Value("${CONTENT_AD2_CATEGORY_ID}")
	private Long CONTENT_AD2_CATEGORY_ID;
	
	@Value("${AD1_HEIGHT}")
	private Integer AD1_HEIGHT;
	@Value("${AD1_HEIGHT_B}")
	private Integer AD1_HEIGHT_B;
	@Value("${AD1_WIDTH}")
	private Integer AD1_WIDTH;
	@Value("${AD1_WIDTH_B}")
	private Integer AD1_WIDTH_B;
	
	/**
	 * 首页内容服务
	 */
	@Autowired
	private ContentService contentService;
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/index")
	public String showIndex(Model model) throws Exception{
		
		//查询出大轮播图的内容，分为大广告类
		List<TbContent> list = contentService.getContentListByCid(CONTENT_AD_CATEGORY_ID);
		
		//封装至AD1Node
		List<AD1Node> nodes = new ArrayList<>();
		for(TbContent content :list) {
			AD1Node node = new AD1Node();
			node.setAlt(content.getTitle());
			node.setHeight(AD1_HEIGHT);
			node.setHeightB(AD1_HEIGHT_B);
			node.setHref(content.getUrl());
			node.setSrc(content.getPic());
			node.setSrcB(content.getPic2());
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_WIDTH_B);
			
			nodes.add(node);
		}
		
		String jsonNode = JsonUtils.objectToJson(nodes);
		//传值前台index界面
		model.addAttribute("ad1", jsonNode);
		
		//乐淘快报
		List<TbContent> kuaiBao = contentService.getContentListByCid(CONTENT_KB_CATEGORY_ID);
		model.addAttribute("kb", kuaiBao);
		
		//中广告
		List<TbContent> ad2List = contentService.getContentListByCid(CONTENT_AD2_CATEGORY_ID);
		List<AD2Node> nodes2 = new ArrayList<>();
		for(TbContent content : ad2List) {
			AD2Node node = new AD2Node();
			node.setAlt(content.getTitle());
			node.setHref(content.getUrl());
			node.setSrc(content.getPic());
			
			nodes2.add(node);
		}
		String jsonNode2 = JsonUtils.objectToJson(nodes2);
		model.addAttribute("ad2", jsonNode2);
		
		
		//前台商品显示
		/*
		 * 数据类型：
		 * 	"1615":{
					"1":
						 {
							"d":"g15\/M00\/13\/1E\/rBEhWFJ4sNUIAAAAAAHJY7c4pHkAAFBugBwkz0AAcl7615.jpg",
							"e":"0",
							"c":"3309.00",
							"a":"1068768",
							"b":"ThinkPad\u54c1\u724c\u60e0,\u6781\u81f4\u6027\u80fd\u5546\u52a1\u672c\uff01",
							"f":1
						}
			...
		 */
		
		Map<String, Map<String,AD3IndexData>> itemMap = new HashMap<>();
		//获取数据1F数据1的数据
		getIndexData(100L,itemMap);
		//获取数据1F数据2的数据
		getIndexData(101L,itemMap);
		//获取数据1F数据3的数据
		getIndexData(102L,itemMap);
		//获取数据1F数据4的数据
		getIndexData(103L,itemMap);
		//获取数据1F数据5的数据
		getIndexData(104L,itemMap);
		String itemJson = JsonUtils.objectToJson(itemMap);
		model.addAttribute("itemJSON", itemJson);
		
		
		return "index";
	}

	/**
	 * 获取首页楼层数据
	 * @return
	 */
	private void getIndexData(Long catetoryId,Map<String, Map<String,AD3IndexData>> itemMap) {
		List<TbContent> indexList = contentService.getContentListByCid(catetoryId);
		Map<String,AD3IndexData> map = new HashMap<>();
		for(int i = 0;i < indexList.size();i++) {
			AD3IndexData ad3 = new AD3IndexData();
			//id
			ad3.setId(indexList.get(i).getTitleDesc());
			//标题
			ad3.setTitle(indexList.get(i).getTitle());
			//图片
			ad3.setImage(indexList.get(i).getPic());
			//价格
			ad3.setPic(indexList.get(i).getSubTitle());
			//清仓标识
			ad3.setPromotion(indexList.get(i).getUrl());
			ad3.setExpress(1);
			
			map.put(String.valueOf(i + 1), ad3);
		}
		
		//最终封装
		//Map<String, Map<String,AD3IndexData>> itemMap = new HashMap<>();
		itemMap.put(catetoryId.toString(), map);
		//String itemJson = JsonUtils.objectToJson(itemMap);
		//return itemMap;
	}
	
}
