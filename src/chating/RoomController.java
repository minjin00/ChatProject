
package chating;

import chating.client.ChatClient;
import chating.server.ChatThread;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RoomController {
    private static int ROOMID = 1;
    private Map<Integer, ChatRoom> roomList;

    private static RoomController instance;

    // private 생성자로 외부에서 인스턴스를 생성하지 못하도록 제한.
    public RoomController() {
        roomList = new HashMap<>();
    }

    // 인스턴스를 반환하는 정적 메서드를 구현합니다.
    public static RoomController getInstance() {
        if (instance == null) {
            instance = new RoomController();
        }
        return instance;
    }


    // 채팅방 생성 후 리스트에 추가
    public void create(ChatThread chatThread) {
        ChatRoom room = new ChatRoom(ROOMID++);
        roomList.put(room.getId(), room);
        //해당 스레드 새 채팅방으로 이동
        room.addChatThread(chatThread);
        System.out.println("[채팅방 생성] 방번호 : " + room.getId());
    }

    // 채팅방 목록 반환
    public Map<Integer, ChatRoom> getRoomList() {
        System.out.println(roomList);
        return roomList;
    }

}
