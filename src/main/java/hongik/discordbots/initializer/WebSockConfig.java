package hongik.discordbots.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket   //이게 websocket 서버로서 동작하겠다는 어노테이션
public class WebSockConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler; // WebSocket 통신을 처리할 핸들러

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket 핸들러를 등록하는 메소드입니다.
        registry.addHandler(webSocketHandler, "/ws/chat")  //"/ws/chat" 경로로 오는 WebSocket 연결을 위해 지정한 핸들러를 등록.
                .setAllowedOrigins("*"); // 모든 도메인에서의 접근을 허용(CORS 문제를 방지). 보안상 좋지않고, 실제환경에서는 공개범위 설정해애함
        // 클라이언트는 지정된 경로("/ws/chat")를 통해 WebSocket 연결을 시도한다.
        // 다른 URL에서도 이 서버의 WebSocket에 접속할 수 있게. 다른 출처(Origin)에서도 WebSocket 연결을 허용합니다.
    }
}