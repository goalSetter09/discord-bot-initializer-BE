package hongik.discordbots.initializer.service.chat;



import com.fasterxml.jackson.databind.ObjectMapper;
import hongik.discordbots.initializer.domain.ChatRoom;
import hongik.discordbots.initializer.repository.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    // final ObjectMapper 객체는 JSON 처리를 위해 사용
    private final ObjectMapper objectMapper;

    // 채팅방 정보를 저장할 Map 객체. (이러면 안돼...)
    //private Map<String, ChatRoom> chatRooms;
    private final ChatRepository chatRooms;

    // 클래스가 인스턴스화되고, 스프링 컨테이너에 의해 관리되기 시작한 후 초기화를 위해 호출되는 메서드.
    /*
    @PostConstruct
    private void init() {
        // LinkedHashMap으로 chatRooms를 초기화. 순서를 유지하며, 채팅방을 관리.
        chatRooms = new LinkedHashMap<>();
    }*/

    // 모든 채팅방을 조회하는 메서드. 채팅방 목록을 List 형태로 반환.
    public List<ChatRoom> findAllRoom() {
        return chatRooms.findAllRoom();
    }

    // 특정 ID의 채팅방을 조회하는 메서드. roomId에 해당하는 ChatRoom 객체를 반환
    public ChatRoom findRoomById(String roomId) {
        return chatRooms.findRoomById(roomId);
    }

    // 새로운 채팅방을 생성하는 메서드. 채팅방 이름을 받아 새로운 채팅방을 생성하고 Map에 추가.
    public ChatRoom createRoom(String name) {
        // 랜덤한 UUID를 생성하여 채팅방 ID로 사용.
        String randomId = UUID.randomUUID().toString();
        // ChatRoom 객체를 생성. (Builder 패턴을 사용)
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(randomId)
                .name(name)
                .build();
        // 생성된 채팅방을 chatRooms Map에 추가.
        chatRooms.save(randomId, chatRoom);
        // 생성된 채팅방 객체를 반환.
        return chatRoom;
    }
}
