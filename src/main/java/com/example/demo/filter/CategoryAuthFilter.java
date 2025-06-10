package com.example.demo.filter;

import java.io.IOException;

import com.example.demo.model.dto.users.UserCert;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@WebFilter(urlPatterns = {"/category/*","/categorys/*"})
public class CategoryAuthFilter extends BaseAuthFilter {
	
	@Override
	protected void doFilter(HttpServletRequest request,HttpServletResponse response,FilterChain chain) 
	throws IOException,ServletException{
		String method = request.getMethod();
		
// 1.首先，無條件放行 OPTIONS 請求 (CORS Preflight)
		
		if("OPTIONS".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}

// 2.開放 GET 查詢
		if("GET".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}
		
// 3. 對於其他方法 (POST, PUT, DELETE 等)，驗證登入狀態
		HttpSession session = request.getSession(false);// 傳 false，如果 session 不存在則不創建新的
// 非 GET 時驗證登入狀態
		if(session != null && session.getAttribute("userCert") != null) {
			UserCert userCert = (UserCert) session.getAttribute("userCert");
			// 檢查是否為 ADMIN 角色
			if(userCert.getPrimaryRole()==UserRole.ADMIN) {
				chain.doFilter(request, response);// 已登入，放行
			}else {
			// 已登入但不是 ADMIN，回傳 403 Forbidden (禁止訪問)
				sendErrorResponse(response,HttpServletResponse.SC_FORBIDDEN,"權限不足，需要管理員權限");
//sendErrorResponse是BaseAuthFilter繼承
//				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//				response.setContentType("application/json;charset=UTF-8");
//				ApiResponse<?> apiResponse = ApiResponse.error(HttpServletResponse.SC_FORBIDDEN,"權限不足，需要管理員權限");
//			// 利用 ObjectMapper 將指定物件轉 json
//				ObjectMapper mapper = new ObjectMapper();
//				String json = mapper.writeValueAsString(apiResponse);
//				response.getWriter().write(json);
			
			}
		}else {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "請先登入");
// 未登入，回傳 401
//			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//			response.setContentType("application/json;charset=UTF-8");
//			ApiResponse<?> apiResponse = ApiResponse.error(401, "請先登入");
//// 利用 ObjectMapper 將指定物件轉 json
//			ObjectMapper mapper = new ObjectMapper();
//			String json = mapper.writeValueAsString(apiResponse);
//			response.getWriter().write(json);

		}

	}


	
	
}
