package com.wangwren.service.pageheapler;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wangwren.mapper.TbItemMapper;
import com.wangwren.pojo.TbItem;
import com.wangwren.pojo.TbItemExample;

/**
 * mybatis分页插件的测试
 * @author wwr
 *
 */
public class PageHelperTest {

	@Test
	public void testPageHelper() throws Exception{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		TbItemMapper mapper = (TbItemMapper) applicationContext.getBean("tbItemMapper");
		
		//使用PageHelper的静态类设置分页的起始页和一页个数
		PageHelper.startPage(1, 10);
		
		TbItemExample example = new TbItemExample();
		
		//执行查询，其中的参数不能写null
		List<TbItem> list = mapper.selectByExample(example);
		
		//用PageInfo对list结果进行包装，pageInfo中包含了很多分页的信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		System.out.println("总记录数:" + pageInfo.getTotal());
		System.out.println("总页数:" + pageInfo.getPages());
		System.out.println(pageInfo.getPageSize());
		System.out.println(pageInfo.getPageNum());
	}
}
