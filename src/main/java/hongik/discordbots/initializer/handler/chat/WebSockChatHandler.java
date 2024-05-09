package hongik.discordbots.initializer.handler.chat;


import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.discordbots.initializer.domain.ChatMessage;
import hongik.discordbots.initializer.domain.ChatRoom;
import hongik.discordbots.initializer.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSockChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper; // JSON 문자열과 Java 객체 간의 변환을 담당.
    private final ChatService chatService; // 채팅방 관련 로직을 처리하는 서비스 객체.


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 클라이언트와 WebSocket 연결이 성공적으로 수립되었을 때 호출.
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 클라이언트로부터 받은 메시지의 본문 가져옴.
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        // JSON 문자열을 ChatMessage 객체로 변환
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());
        // 메시지에 명시된 방 ID를 기반으로 채팅방을 조회.

        Set<WebSocketSession> sessions=room.getSessions();   // 해당 채팅방에 있는 모든 사용자의 WebSocket 세션.

        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
            // 사용자가 채팅방에 입장했을 시
            sessions.add(session); // 새로운 사용자의 세션을 채팅방 세션 목록에 추가.
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");  // 사용자 입장 메시지를 설정.
            sendToEachSocket(sessions,new TextMessage(objectMapper.writeValueAsString(chatMessage)) );
            // 채팅방의 모든 사용자에게 입장 메시지를 전송.
        }
        else if (chatMessage.getType().equals(ChatMessage.MessageType.QUIT)) {
            // 사용자가 채팅방에서 퇴장했을 시.
            sessions.remove(session); // 퇴장한 사용자의 세션을 채팅방 세션 목록에서 제거.
            chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장했습니다..");
            // 사용자 퇴장 메시지를 설정.
            sendToEachSocket(sessions,new TextMessage(objectMapper.writeValueAsString(chatMessage)) );
            // 채팅방의 모든 사용자에게 퇴장 메시지를 전송.
        }
        else {
            // 사용자의 메시지가 입장이나 퇴장이 아닌 경우의 로직. 즉 현재 있는 경우! 일반 메시지 전송 처리.
            sendToEachSocket(sessions,message ); // 클라이언트로부터 받은 메시지를 그대로 채팅방의 모든 사용자에게 전송.
        }
    }

    private void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message){
        // 주어진 메시지를 채팅방의 모든 세션에게 전송
        sessions.parallelStream().forEach( roomSession -> {
            try {
                roomSession.sendMessage(message); // 각 세션에 메시지를 전송
            } catch (IOException e) {
                throw new RuntimeException(e); // 메시지 전송 중 오류 발생 시, 런타임 예외를 발생.
            }
        });
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 클라이언트와의 WebSocket 연결이 닫혔을 때 호출
    }



}
