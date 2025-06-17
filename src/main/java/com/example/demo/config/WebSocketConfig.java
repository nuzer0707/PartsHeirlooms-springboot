package com.example.demo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;



@Configuration
@EnableWebSocketMessageBroker// 啟用 WebSocket 訊息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	
	@Autowired
    private HttpHandshakeInterceptor handshakeInterceptor; 
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		registry.addEndpoint("/ws-connect") // <--- 使用一個專用且清晰的路徑
        .setAllowedOriginPatterns("*")
        .addInterceptors(handshakeInterceptor); // <--- 在此處加上攔截器
			
		
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry  registry) {
		 // 配置一個訊息代理，用於將訊息從一個用戶端路由到另一個用戶端。
    
    // "/topic" 和 "/queue" 是代理的目的地前綴。
    // 發送到這些前綴的目的地的訊息將被路由到訊息代理。
    // "/topic" 通常用於一對多的廣播。
    // "/queue" 通常用於一對一的訊息。
		registry.enableSimpleBroker("/topic", "/queue");
		// "/app" 是應用程式目的地前綴。
    // 用戶端發送的訊息如果目的地前綴是 "/app"，將會被路由到 @MessageMapping 註解的方法進行處理。
		registry.setApplicationDestinationPrefixes("/app");
		
	}

	
	
}
