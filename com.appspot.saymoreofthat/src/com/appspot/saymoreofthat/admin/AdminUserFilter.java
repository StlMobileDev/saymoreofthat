package com.appspot.saymoreofthat.admin;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AdminUserFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		UserService userService = UserServiceFactory.getUserService();
		if ((userService.getCurrentUser() != null) && userService.isUserAdmin()) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
			HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
			httpServletResponse.sendRedirect(userService
					.createLoginURL(httpServletRequest.getRequestURI()));
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
