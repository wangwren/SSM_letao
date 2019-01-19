package com.wangwren.protal.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.wangwren.protal.pojo.AD1Node;
import com.wangwren.protal.pojo.AD2Node;
import com.wangwren.service.ItemCatService;

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
		
		return "index";
	}
	
}
