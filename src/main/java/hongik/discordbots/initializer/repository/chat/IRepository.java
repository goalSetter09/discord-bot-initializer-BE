package hongik.discordbots.initializer.repository.chat;



import hongik.discordbots.initializer.domain.ChatRoom;

import java.util.List;

public interface IRepository {

    ChatRoom findRoomById(String roomId);

    List<ChatRoom> findAllRoom();

    void save(String id, ChatRoom room);
}
