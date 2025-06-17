package com.example.demo.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.demo.model.dto.users.UserCert;

import jakarta.servlet.http.HttpSession;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor  {

	@Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false); // 不自動創建新 session

            if (session != null) {
                UserCert userCert = (UserCert) session.getAttribute("userCert");
                if (userCert != null) {
                    // 如果用戶已登入 (session 中有 userCert)
                    // 1. 將 userCert 放入 WebSocket 的 session attributes 中
                    attributes.put("userCert", userCert);
                    // 2. 允許握手，繼續建立 WebSocket 連接
                    return true;
                }
            }
        }
        
        // 如果 session 不存在，或 session 中沒有 userCert，則拒絕握手
        // 這裡可以設置 HTTP 狀態碼，但更簡單的方式是直接返回 false
        // response.setStatusCode(HttpStatus.UNAUTHORIZED); // 這行可能不會如預期工作
        System.out.println("WebSocket Handshake Rejected: User not logged in.");
        return false;
    }

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
