package com.wangwren.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wangwren.common.pojo.SearchResult;
import com.wangwren.search.service.SearchService;

/**
 * 商品搜索
 * @author wwr
 *
 */
@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	/**
	 * 每页显示条数
	 */
	@Value("${SEARCH_RESULT_ROWS}")
	private Integer SEARCH_RESULT_ROWS;
	
	/**
	 * 搜索功能，返回搜索界面
	 */
	@RequestMapping("/search")
	public String search(@RequestParam("q")String queryString,@RequestParam(defaultValue="1")Integer page,Model model) throws Exception{
		//int a = 1 / 0; 全局异常测试
		//queryString解决get方式乱码
		queryString = new String(queryString.getBytes("ISO8859-1"), "UTF-8");
		SearchResult searchResult = searchService.search(queryString, page, SEARCH_RESULT_ROWS);
		//数据回显，显示查询结果
		model.addAttribute("query", queryString);
		model.addAttribute("totalPages", searchResult.getTotalPages());
		model.addAttribute("itemList", searchResult.getSearchItems());
		model.addAttribute("page", page);
		
		//返回视图界面
		return "search";
	}
	
	/**
	 * 通过商品分类id查询该分类下的所有商品
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search/item/cat")
	public String searchByCid(String cname,@RequestParam(defaultValue="1")Integer page,Model model) throws Exception{
		//queryString解决get方式乱码
		cname = new String(cname.getBytes("ISO8859-1"), "UTF-8");
		SearchResult searchResult = searchService.searchByCname(cname, page, SEARCH_RESULT_ROWS);
		//数据回显，显示查询结果
		model.addAttribute("query", cname);
		model.addAttribute("totalPages", searchResult.getTotalPages());
		model.addAttribute("itemList", searchResult.getSearchItems());
		model.addAttribute("page", page);
		
		//返回视图界面
		return "search";
	}
}
