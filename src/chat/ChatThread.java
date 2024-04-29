package chat;

import Ex.RoomController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

class ChatThread extends Thread {
    private Socket socket;
    private String nickname;
    private BufferedReader in;
    private PrintWriter out;
    private Map<String, PrintWriter> chatClients;
    private static Map<String, Integer> userRooms;
    private static Map<Integer, List<ChatThread>> roomList = new HashMap<>();
    private static int ROOMID = 1;

    static {
        roomList.put(ROOMID, new ArrayList<>());
    }


    public ChatThread(Socket socket, Map<String, PrintWriter> chatClients, Map<String, Integer> userRooms) {
        this.socket = socket;
        this.chatClients = chatClients;
        this.userRooms = userRooms;

        //클라이언트 생성하면서 닉네임 받아오기, ip주소 출력
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            nickname = in.readLine(); //닉네임 입력

            //채팅 동시 처리하기
            synchronized (chatClients) {
                chatClients.put(this.nickname, out);
                userRooms.put(this.nickname, ROOMID);
                roomList.get(ROOMID).add(this);
            }
            broadcast(nickname + "닉네임의 사용자가 연결했습니다.");
            System.out.println(nickname + "님 IP 주소 : " + socket.getInetAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println(nickname + "님이 채팅을 시작합니다.");
        String msg = null;

        try {
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("/")) {
                    handleCommand(msg);
                } else {
                    broadcast(nickname + " : " + msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (in != null) {
//            try {
//                in.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        if (socket != null) {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    public void handleCommand(String msg) {
        try {
            // /list -> 방 목록 보기
            if ("/list".equalsIgnoreCase(msg)) {
                list();
            }
            // /create -> 방 생성
            if ("/create".equalsIgnoreCase(msg)) {
                create(this);
            }
            // /join [방번호] -> 방 입장
            if (msg.startsWith("/join")) {
                // join 뒤에 숫자 받아 오기
                // 메시지에서 방 번호를 추출합니다.
                int roomId;
                String[] parts = msg.split(" ");
                if (parts.length != 2) {
                    System.out.println("잘못된 명령어 형식입니다. 사용법: /join 방번호");
                    return;
                }
                String roomIdStr = parts[1].trim(); // 방 번호 문자열 추출
                try {
                    roomId = Integer.parseInt(roomIdStr) - 1; // 사용자 입력을 배열 인덱스에 맞게 조정
                    join(roomId); // 사용자를 방에 입장시킵니다.
                    System.out.println(nickname + " 님이" + (roomId + 1) + "번 방에 입장하셨습니다.");
                    roomSend(roomId, nickname + " 님이" + (roomId + 1) + "번 방에 입장하셨습니다."); // 방에 입장한 것을 알립니다.
                } catch (NumberFormatException e) {
                    System.out.println("잘못된 방 번호입니다.");
                    return;
                }
            }

            //exit -> 방 나가기
//                if ("/exit".equalsIgnoreCase(msg)) {
//                    exit();
//                }
        } catch (Exception e) {
            e.printStackTrace();
//        } finally {
//            broadcast(nickname + "닉네임의 사용자가 연결을 끊었습니다.");
//            synchronized (chatClients) {
//                chatClients.remove(nickname);
//            }
        }

    }

    public void broadcast(String msg) {
        synchronized (chatClients) {
            Iterator<PrintWriter> it = chatClients.values().iterator();
            while (it.hasNext()) {
                PrintWriter out = it.next();
                try {
                    out.println(msg);
                } catch (Exception e) {
                    it.remove();
                    e.printStackTrace();
                }
            }
        }
    }
    public void create(ChatThread chatThread) {
        ROOMID++; // 방 +1
        List<ChatThread> roomMembers = new ArrayList<>(); //유저 리스트 생성
        roomMembers.add(chatThread); //입장 후 유저 리스트에 추가
        roomList.put(ROOMID, roomMembers); //생성된 방을 방 리스트에 추가
        userRooms.put(this.nickname, ROOMID); //속해있는 방 업데이트
        System.out.println(ROOMID + "번 방 생성 완료");
    }

    // 방 목록 불러오기
    private void list() {
        synchronized (roomList) {
            // 방 목록이 비어 있는지 확인
            System.out.println("현재 방 목록:");
            //Set<Integer> uniqueRoomNumbers = new HashSet<>(userRooms.values());
            for (Integer roomNumber : roomList.keySet()) {
                System.out.println(roomNumber);
            }
        }
    }

    // 방에 입장하기
    public static void join(int roomID, ChatThread chatThread) {
        Integer currentRoom = userRooms.get(chatThread.nickname);
        if (currentRoom != null && roomList.get(currentRoom) != null) {
            roomList.get(currentRoom).remove(chatThread);
        }
        // 방번호에 해당하는 방의 멤버 리스트를 가져옴
        List<ChatThread> roomMembers = roomList.computeIfAbsent(roomID, k -> new ArrayList<>());
        // 해당 방이 존재하지 않으면 메시지 출력
        if (roomMembers == null) {
            System.out.println("방이 존재하지 않습니다.");
        } else {
            // 방 멤버 리스트에 사용자 추가
            roomMembers.add(chatThread);
            chatThread.userRooms.put(chatThread.nickname, roomID);
            System.out.println(chatThread.nickname + "님이 " + (ROOMID -1) + "번 방에 입장하셨습니다.");
        }
    }

    //같은 방 사람들끼리 보내기
    public void roomSend(int roomID, String msg) {
        List<ChatThread> roomMembers = roomList.get(roomID);
        synchronized (roomList) {
            for (ChatThread member : roomMembers) {
                if(!member.nickname.equals(this.nickname)) {
                    member.out.println(nickname + " : " + msg);
                }
            }
        }
    }

    // 방 나간 후 리스트 삭제
//    public static void exit(int ROOMID, ChatThread chatThread) {
//        // 해당 방의 멤버 리스트 가져오기
//        List<ChatThread> roomMembers = userRooms.get(ROOMID);
//        if (roomMembers != null) {
//            // 사용자를 방 멤버 리스트에서 제거
//            roomMembers.remove(chatThread);
//            System.out.println(chatThread.nickname + "님이 " + ROOMID + "번 방에서 퇴장하셨습니다.");
//            // 다른 멤버들에게 퇴장 알림
//            for (ChatThread member : roomMembers) {
//                member.roomSend(chatThread.nickname + "님이 방을 떠났습니다.");
//            }
//            // 방의 멤버가 없다면 방을 삭제
//            if (roomMembers.isEmpty()) {
//                userRooms.remove(ROOMID);
//                System.out.println(ROOMID + "번 방이 삭제되었습니다.");
//            }
//        } else {
//            System.out.println("오류: " + ROOMID + "번 방이 존재하지 않습니다.");
//        }
//    }
}




