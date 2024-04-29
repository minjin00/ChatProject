package Ex;

import java.util.Iterator;
import java.util.List;
public class ChatRoomService {
    private static int ROOMID = 1;
    private List<ChatRoom> chatRoomList;

//    public ChatRoom createChatRoom(String title) {
//      //  ChatRoom chatRoom = new ChatRoom(ROOMID, title);
//      //  return chatRoom;
//    }

    public Iterator<ChatRoom> getChatRoomIterator() {
        return chatRoomList.iterator();
    }
}
