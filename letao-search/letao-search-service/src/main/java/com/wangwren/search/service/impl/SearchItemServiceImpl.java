package com.wangwren.search.service.impl;
/**
 * 商品索引库相关
 * 
 * 后台导入索引
 * @author wwr
 *
 */

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.pojo.SearchItem;
import com.wangwren.search.mapper.SearchItemMapper;
import com.wangwren.search.service.SearchItemService;

@Service
public class SearchItemServiceImpl implements SearchItemService {

	/**
	 * 记得再加一个扫描包
	 */
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	/**
	 * 连接solr服务器对象
	 */
	@Autowired
	private SolrServer solrServer;
	
	/**
	 * 查询全部商品，并添加至索引库中
	 */
	@Override
	public LetaoResult importSolrIndex() {

		try {
			//查询出所有商品
			List<SearchItem> list = searchItemMapper.getAllSearchItem();
			
			//遍历，导入索引库
			for(SearchItem searchItem : list) {
				//创建solrInputDocument对象
				SolrInputDocument document = new SolrInputDocument();
				//添加域
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCat_name());
				
				//添加至solr服务器中
				solrServer.add(document);
			}
			//提交事务
			solrServer.commit();
		}catch (Exception e) {
			e.printStackTrace();
			return LetaoResult.build(500, "导入失败...");
		}
		
		
		return LetaoResult.ok();
	}

}
