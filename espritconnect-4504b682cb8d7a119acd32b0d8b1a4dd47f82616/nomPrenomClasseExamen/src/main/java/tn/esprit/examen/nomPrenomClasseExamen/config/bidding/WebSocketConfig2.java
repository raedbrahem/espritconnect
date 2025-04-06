package tn.esprit.examen.nomPrenomClasseExamen.config.bidding;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig2 implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register the WebSocket handler that will handle incoming WebSocket messages
        registry.addHandler(new BidWebSocketHandler(), "/bidding")
                .setAllowedOrigins("*"); // You can restrict the origins as needed
    }
}
