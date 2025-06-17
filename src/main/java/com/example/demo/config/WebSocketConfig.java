package com.example.demo.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;



@Configuration
@EnableWebSocketMessageBroker// 啟用 WebSocket 訊息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
	// 註冊一個 STOMP 端點，用戶端將使用它來連接到 WebSocket 伺服器。
    // "/ws" 是連接的端點 URL。
    // withSockJS() 是為不支援 WebSocket 的瀏覽器提供備用選項。
		
		registry.addEndpoint("/ws")
			.setAllowedOriginPatterns("http://localhost:5173", "http://localhost:8002")
			//.setAllowedOriginPatterns("*")
			.withSockJS();
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
