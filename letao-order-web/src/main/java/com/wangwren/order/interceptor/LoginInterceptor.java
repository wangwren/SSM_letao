package com.wangwren.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wangwren.common.pojo.LetaoResult;
import com.wangwren.common.utils.CookieUtils;
import com.wangwren.pojo.TbUser;
import com.wangwren.sso.service.UserService;
/**
 * 用户登录的拦截器
 * @author wwr
 *
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Autowired
	private UserService userService;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex)
			throws Exception {
		// 在handler(handler表示方法)执行之后，modelAndView执行之后，执行该方法，一般做异常处理

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView)
			throws Exception {
		// 在handler执行之后，modelAndView执行之前执行该方法

	}

	/**
	 * 用户登录拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		// 在handler执行之前，执行该方法
		//先从cookie中取出token信息
		String token = CookieUtils.getCookieValue(request, TOKEN_KEY);
		
		//如果token不存在，则拦截，返回登录页面，并携带当前页面的url
		if(StringUtils.isBlank(token)) {
			//如果token为空，拦截
			String requestURL = request.getRequestURL().toString();
			//跳转至登录页面
			response.sendRedirect(SSO_URL + "/page/login?url=" + requestURL);
			return false;
		}
		
		//如果token存在，则调用sso的getUserByToken(token)服务来判断是否有用户信息，如果没有，则拦截，与上一步操作相同
		LetaoResult letaoResult = userService.getUserByToken(token);
		//判断letaoResult中的状态
		if(letaoResult.getStatus() != 200) {
			String requestURL = request.getRequestURL().toString();
			response.sendRedirect(SSO_URL + "/page/login?url=" + requestURL);
			return false;
		}
		//如果查到用户信息，则放行，并将用户信息存到request域中，带给handler
		TbUser user = (TbUser) letaoResult.getData();
		request.setAttribute("user", user);
		//返回true:放行     返回false:拦截
		return true;
	}

}
