package com.example.demo.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@WebFilter(urlPatterns = {"/profile/*"}) // <--- 修改於此，保護所有 /profile/ 下的路徑
public class ProfileApiAuthFilter extends BaseAuthFilter{

	
	@Override
	protected void doFilter(HttpServletRequest request,HttpServletResponse response,FilterChain chain) 
	throws IOException,ServletException{
		String method = request.getMethod();
	
		// 1. OPTIONS 請求放行 (CORS Preflight)
		if("OPTIONS".equalsIgnoreCase(method)) {
			chain.doFilter(request, response);
			return;
		}
	
    // 2. 檢查登入狀態
		HttpSession session = request.getSession(false);// 傳 false，如果 session 不存在則不創建新的
		if(session != null && session.getAttribute("userCert")!=null) {
			// UserCert userCert = (UserCert) session.getAttribute("userCert");
      // 如果未來需要根據角色或其他 userCert 中的資訊進行更細緻的控制，可以在這裡添加邏輯
      // 例如：if (userCert.getRole() == UserRole.BUYER || userCert.getRole() == UserRole.SELLER)
      // 對於 /profile/* 路徑，目前只要登入即可
      // 如果特定子路徑需要特定 HTTP 方法，可以在 Controller 層面處理或在這裡加入更複雜的判斷
      // 例如，你之前的判斷 "/profile/password" 只允許 PUT
			String path = request.getRequestURI();
			if(path.endsWith("/password") && !"PUT".equalsIgnoreCase(method)) {
				sendErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "此路徑僅支援 PUT 方法");
				return;
			}
			chain.doFilter(request, response);// 已登入，放行
		}else {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "請先登入以訪問此資源");
		}
		
	}
	
	
	
	
}
