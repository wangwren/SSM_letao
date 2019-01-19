package com.wangwren.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * FreeMarker测试
 * @author wwr
 *
 */
public class FreeMarkerTest {

	@Test
	public void testFreeMarker() throws Exception{
		//创建一个模板文件
		//1.创建一个Configuration对象，需要指定构造参数，参数为freemarker的版本号
		Configuration configuration = new Configuration(Configuration.getVersion());
		//2.设置模板所在路径
		configuration.setDirectoryForTemplateLoading(new File("E:\\workspace\\eclipsespace\\letao-item-web\\src\\main\\webapp\\WEB-INF\\ftl"));
		//3.设置模板的字符集，一般为utf-8
		configuration.setDefaultEncoding("UTF-8");
		//4.使用configuration对象加载一个模板对象，需要指定模板的文件名，并获取模板对象
		Template template = configuration.getTemplate("student.ftl");
		//5.创建数据集，可以是pojo或map，一般为map
		Map data = new HashMap();
		//Student stu = new Student(1, "刘威", 23, "辽宁省丹东市");
		//data.put("stu", stu);
		
		List<Student> stuList = new ArrayList<>();
		stuList.add(new Student(1, "刘威", 23, "辽宁省丹东市"));
		stuList.add(new Student(2, "刘威2", 23, "辽宁省丹东市"));
		stuList.add(new Student(3, "刘威3", 23, "辽宁省丹东市"));
		stuList.add(new Student(4, "刘威4", 23, "辽宁省丹东市"));
		stuList.add(new Student(5, "刘威5", 23, "辽宁省丹东市"));
		
		data.put("stuList", stuList);
		
		//日期类型测试
		data.put("date", new Date());
		
		//6.创建一个Writer对象，指定生成的文件的路径和文件名
		Writer writer = new FileWriter("C:\\Users\\wwr\\Desktop\\毕设\\out\\student.html");
		//7.使用模板对象的process方法输出文件
		template.process(data, writer);
		//8.关闭流
		writer.close();
	}
}
