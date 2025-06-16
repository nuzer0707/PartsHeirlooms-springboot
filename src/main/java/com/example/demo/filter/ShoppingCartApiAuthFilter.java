package com.example.demo.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebFilter(urlPatterns = { "/cart/*" })
public class ShoppingCartApiAuthFilter extends BaseAuthFilter{
	
	
	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String method = request.getMethod();

		// 1. OPTIONS 請求放行
		if ("OPTIONS".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}

		// 2. 驗證登入狀態
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("userCert") != null) {
			
			chain.doFilter(request, response);
		} else {
			// 未登入
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "請先登入才能使用");
		}

	}

}
