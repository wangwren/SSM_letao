package com.wangwren.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.search.service.SearchItemService;

/**
 * 全文检索索引库管理
 * @author wwr
 *
 */
@Controller
public class SearchItemController {

	@Autowired
	private SearchItemService searchItemService;
	
	/**
	 * 将商品数据全部导入索引库
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/index/import")
	@ResponseBody
	public LetaoResult importSolrIndex() throws Exception{
		LetaoResult result = searchItemService.importSolrIndex();
		return result;
	}
}
