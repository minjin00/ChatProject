
package Ex;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomController {
    static int ROOMID = 1; //방 번호, 초기값은 1
    //ChatClient chatClient; //socket, nickname, in, out
    static Map<String, PrintWriter> chatClients;
    //static Map<Integer, List<ChatThread>> roomList = new HashMap<>(); //방 저장 리스트(ROOMID, chatClient)
    private static Map<Integer, List<PrintWriter>> roomList = new HashMap<>();

//    // 방 입장 후 리스트에 추가
    public static void create(ChatThread chatThread) {
        List<ChatThread> roomMembers = new ArrayList<>(); //유저 리스트
        roomMembers.add(chatThread); //입장 후 유저 리스트에 추가
       // roomList.put(ROOMID, roomMembers); //방 리스트에 추가
        System.out.println(ROOMID+ "번 방 생성 완료");
        ROOMID++;
    }

//    public static void create(PrintWriter client) {
//        List<PrintWriter> clients = new ArrayList<>();
//        clients.add(client);
//        roomList.put(ROOMID, clients);
//        chatClients.put(client.toString(), ROOMID);
//        client.println("Room " + ROOMID + " has been created.");
//        ROOMID++;
//    }


    // 방에 입장하기
// 방에 입장하기
    public static void join(int ROOMID, ChatThread chatThread) {
        // 방번호에 해당하는 방의 멤버 리스트를 가져옴
        List<ChatThread> roomMembers = new ArrayList<>();
        // 해당 방이 존재하지 않으면 메시지 출력
        if (roomMembers == null) {
            System.out.println("방이 존재하지 않습니다.");
        } else {
            // 방 멤버 리스트에 사용자 추가
            roomMembers.add(chatThread);
            System.out.println(chatThread.getNickname() + "님이 " + ROOMID + "번 방에 입장하셨습니다.");
        }
    }

    // 방 나간 후 리스트 삭제
//    public static void exit(int ROOMID, ChatThread chatThread) {
//       // List<ChatThread> roomMembers = roomList.get(ROOMID);
//        if (roomMembers != null) {
//            roomMembers.remove(chatThread);
//            System.out.println(ROOMID + "번 방 퇴장완료");
//        }
//    }

    // 방 목록 불러오기
//    public void list() {
//        if(roomList.isEmpty()) {
//            System.out.println("방이 비어있습니다.");
//        } else {
//            for(Map.Entry<String, Integer> entry : roomList.entrySet()) {
//                System.out.println("방 번호 : " + entry.getValue());
//            }
//        }
//    }
}
