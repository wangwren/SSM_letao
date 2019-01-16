package com.wangwren.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
/**
 * 全局异常处理器
 * 还需要在springMVC配置文件中配置这个类
 */
public class GlobalExceptionResolver implements HandlerExceptionResolver {

	/**
	 * 使用slf4j创建日志对象
	 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception e) {

		logger.info("进入全局异常处理器...");
		
		//将错误信息打印至控制台
		e.printStackTrace();
		
		//将错误信息打印到日志文件中
		logger.error("系统发生异常", e);;
		
		//可以发邮件或发短信给开发人员
		
		//返回提示信息和错误界面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", "系统发生异常，请稍后重试...");
		modelAndView.setViewName("error/exception");
		
		return modelAndView;
	}

}
