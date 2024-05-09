package hongik.discordbots.initializer.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;


import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatRoom {
    private String roomId;
    private String name;

    // WebSocketSession 객체들의 집합을 저장하는 세트.
    // 여기서 웹소켓 세션을 관리 각 WebSocketSession은 클라이언트의 웹소켓 연결을 대표하며, 이 세트를 통해
    // 서버에 연결된 모든 클라이언트의 세션을 추적하고 관리.
    private Set<WebSocketSession> sessions = new HashSet<>();
    @Builder
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }
}