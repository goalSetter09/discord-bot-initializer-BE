package hongik.discordbots.initializer.repository.chat;


import hongik.discordbots.initializer.domain.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChatRepository implements IRepository {

    private Map<String, ChatRoom> chatRooms = new HashMap<>();

    @Override
    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }
    @Override
    public List<ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }

    @Override
    public void save(String id, ChatRoom room){
        chatRooms.put(id, room);
    }
}
