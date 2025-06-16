package com.example.demo.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

 
@WebFilter(urlPatterns = { "/messages/*" })
public class MessageApiAuthFilter extends BaseAuthFilter {

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
			// 已登入，放行請求到 MessageController
			// MessageController 內部的方法會根據業務邏輯進行更細緻的權限判斷
			// (例如，是否能查看某個對話，是否能給某人發消息等)
			chain.doFilter(request, response);
		} else {
			// 未登入
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "請先登入才能使用消息功能");
		}

	}

}
