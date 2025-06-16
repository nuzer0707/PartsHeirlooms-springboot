package com.example.demo.filter;

import java.io.IOException;

import com.example.demo.model.dto.users.UserCert;
import com.example.demo.model.entity.enums.UserRole;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/seller/*" })
public class SellerApiAuthFilter extends BaseAuthFilter {

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String method = request.getMethod();

		// 1. OPTIONS 請求放行 (CORS Preflight)
		if ("OPTIONS".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}

		// 2. 驗證登入狀態和角色
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("userCert") != null) {
			UserCert userCert = (UserCert) session.getAttribute("userCert");
			if (userCert.getPrimaryRole() == UserRole.SELLER || userCert.getPrimaryRole() == UserRole.ADMIN) {
				chain.doFilter(request, response);
			} else {
				// 已登入但不是 SELLER 或 ADMIN
				sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "權限不足，需要賣家或管理員權限才能訪問此功能");
			}

		} else {
			// 未登入
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "請先登入以訪問賣家功能");
		}

	}

}