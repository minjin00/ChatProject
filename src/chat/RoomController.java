//package chat;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class RoomContraoller {
//    static int ROOMID = 1; //방 번호
//    ChatClient chatClient; //socket, nickname, in, out
//    Map<Integer, ChatClient> roomList = new HashMap<>(); //방 저장 리스트(ROOMID, chatClient)
//
//    // 방 입장 후 리스트에 추가
//    public void create() {
//        Rooms room = new Rooms(chatClient, ROOMID); // 방 생성
//        roomList.put(ROOMID++, chatClient); //리스트 추가하면서 방 번호 1씩 추가
//    }
//
//    // 방에 입장하기
////    public void join(int ROOMID, ChatClient chatClient) {
////        List<ChatClient> roomMember = roomList.get(ROOMID);
////        if (roomMember == null) {
////            System.out.println("방이 존재하지 않습니다.");
////        }
////        roomMember.add(chatClient);
////        System.out.println(chatClient.getNickname() + "님이 방에 입장하셨습니다.");
////    }
//
//    // 방 나간 후 리스트 삭제
//    public void exit() {
//        ChatClient remove = roomList.remove(ROOMID--);
//        try {
//            // 방 번호가 1 이하 일 경우 예외처리
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 방 목록 불러오기
//    public void AllList() {
//        for (Map.Entry<Integer, ChatClient> integerChatClientEntry : roomList.entrySet()) {
//            System.out.println(integerChatClientEntry.getKey() + " : " + integerChatClientEntry.getValue());
//        }
//    }
//
//
//
//
//
//
//
//}
//
//
//
