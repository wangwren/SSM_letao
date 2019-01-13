package com.wangwren.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wangwren.common.utils.JsonUtils;
import com.wangwren.utils.FastDFSClient;

/**
 * 图片上传的controller
 * 图片上传也不及涉及后台服务，在表现层中实现就行
 * 
 * 使用SpringMVC实现图片上传，需要用到commons-io和fileupload两个jar包
 * 
 * 还需要在配置文件中声明图片上传解析器
 * @author wwr
 *
 */
@Controller
public class PictureController {

	@Value("${PICTUER_SERVICE_URL}")
	private String PICTUER_SERVICE_URL;
	
	/**
	 * 图片上传
	 * 使用的是kindeditor的上传控件
	 * 
	 * /pic/upload 也是在common.js中指定的
	 * 
	 * MultipartFile是SpringMVC的文件上传接口
	 * 
	 * kindeditor上传图片返回的数据格式：
	 * 		//成功时
			{
			        "error" : 0,
			        "url" : "http://www.example.com/path/to/file.ext"
			}
			//失败时
			{
			        "error" : 1,
			        "message" : "错误信息"
			}
			
		responseBody 其实就是将返回结果使用response对象输出
		SpringMVC会将返回对象以json格式返回
		如果返回String，就默认返回String，浏览器会接收String
	 * @return
	 */
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String picUpload(MultipartFile uploadFile) {
		try {
			//获取文件的原始文件名称
			String originalFilename = uploadFile.getOriginalFilename();
			//截取文件的扩展名
			String ext = originalFilename.substring(originalFilename.indexOf(".") + 1);
			
			//上传到图片服务器
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/client.conf");
			//使用流的方式上传，因为这时指定不了文件路径，是通过流的方式得到的图片
			String url = fastDFSClient.uploadFile(uploadFile.getBytes(), ext);
			url = PICTUER_SERVICE_URL + url;
			
			//上传成功
			Map result = new HashMap();
			result.put("error", 0);
			result.put("url", url);
			
			return JsonUtils.objectToJson(result);
			
		}catch (Exception e) {
			e.printStackTrace();
			//上传失败
			Map result = new HashMap();
			result.put("error", 1);
			result.put("message", "错误信息");
			
			return JsonUtils.objectToJson(result);
		}
		
	}
}
