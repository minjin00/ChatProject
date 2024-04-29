package Ex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomController {
    static int ROOMID = 1; //방 번호, 초기값은 1
    chat.ChatClient chatClient; //socket, nickname, in, out
    static Map<Integer, ChatClient> roomList = new HashMap<Integer, ChatClient>(); //방 저장 리스트(ROOMID, chatClient)

    // 방 입장 후 리스트에 추가
    public static void create(ChatThread chatClient) {
        List<ChatThread> roomMembers = new ArrayList<>(); //유저 리스트
        roomMembers.add(chatClient); //입장 후 유저 리스트에 추가
        roomList.put(ROOMID++, (ChatClient) roomMembers); //방 리스트에 추가
        System.out.println((ROOMID) + "번 방 생성 완료");
    }

    // 방에 입장하기
    public static void join(int ROOMID, ChatThread chatClient) {
        ChatClient roomMembers = roomList.get(ROOMID);
        if (roomMembers == null) {
            System.out.println("방이 존재하지 않습니다.");
        }
        //roomMembers.add(chatClient);
//        System.out.println(chatClient.getNickname() + "님이" + ROOMID + "번 방에 입장하셨습니다.");
    }

    // 방 나간 후 리스트 삭제
    public static void exit(int ROOMID, ChatClient chatClient) {
        List<ChatClient> roomMembers = (List<ChatClient>) roomList.get(ROOMID);
        if(roomMembers != null) {
            roomMembers.remove(chatClient);
            System.out.println(ROOMID + "번 방 퇴장완료");
        }
    }

    // 방 목록 불러오기
    public static void AllList() {
        if(roomList.isEmpty()) {
            System.out.println("방이 존재하지 않습니다.");
            return;
        }
        for(Map.Entry<Integer, ChatClient> entry : roomList.entrySet()) {
            System.out.println(entry.getKey() + "번 방 : ");
            //for(ChatClient client : entry.getValue()) {
                System.out.println(ROOMID + " ");
            }
            System.out.println();
        }

}





