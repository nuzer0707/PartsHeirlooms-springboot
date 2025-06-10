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

@WebFilter(urlPatterns = {"/products/*"})
public class ProductApiAuthFilter extends BaseAuthFilter {
	
	@Override
	protected void doFilter(HttpServletRequest request,HttpServletResponse response,FilterChain chain) 
		throws IOException,ServletException{
		String method = request.getMethod();
		
		// 1. OPTIONS 請求放行 (CORS Preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }
        
		
        // 2. GET 請求通常是公開的，直接放行 (更細緻的GET權限在Controller或Service層處理，例如 /products/my)
        if ("GET".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 3. 對於 POST, PUT, DELETE 等修改操作，驗證登入狀態和權限
		HttpSession session =request.getSession(false);
		
        if (session !=null ) {
			UserCert userCert = (UserCert) session.getAttribute("userCert");
		
			// 刊登商品(POST): 必須是 SELLER 或 ADMIN
			if("POST".equalsIgnoreCase(method)) {
				if(userCert.getPrimaryRole()==UserRole.SELLER||userCert.getPrimaryRole()==UserRole.ADMIN) {
					 chain.doFilter(request, response);
				}else {
					sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "權限不足，只有賣家或管理員可以刊登商品");
				}
				return;
			}
			// 修改(PUT)或刪除(DELETE)商品: 權限檢查會在Service層根據是否為擁有者或ADMIN進行
            // Filter層只確保已登入。也可以在此檢查是否為 SELLER 或 ADMIN，但擁有者判斷較複雜，放Service層更好。
			
			if("PUT".equalsIgnoreCase(method)|| "DELETE".equalsIgnoreCase(method)) {
				if(userCert.getPrimaryRole()==UserRole.SELLER||userCert.getPrimaryRole()==UserRole.ADMIN) {
					 chain.doFilter(request, response);
				}else {
					sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "權限不足，只有賣家或管理員可以刊登商品");
				}
				return;
			}
			 chain.doFilter(request, response);
        }else {
        	// 未登入，回傳 401
        	sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "請先登入以執行此操作");

        	
        }
        
		
	}

}
