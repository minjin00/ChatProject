
package chating.server;

import chating.ChatRoom;
import chating.RoomController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

public class ChatThread extends Thread{
    // 생성자를 통해 클라이언트 소켓을 얻어옴,
    private Socket socket;
    private String id;
    private Map<String, PrintWriter> chatClients;

    BufferedReader in = null;

    public ChatThread(Socket socket, Map<String, PrintWriter> chatClients) {
        this.socket = socket;
        this.chatClients = chatClients;

        // 클라이언트가 생성될 때 클라이언트로 부터 아이디를 얻어오게
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            id = in.readLine();

            broadcast(id + "님이 입장하셨습니다.");
            System.out.println("사용자의 아이디는 " + id + " / IP address : " + socket.getInetAddress());

            // 동시에 일어날 수도
            synchronized (chatClients) {
                chatClients.put(this.id, out);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();

        }
    }
    // run
    @Override
    public void run() {
        // 연결된 클라이언트가 메시지를 전송하면, 그 메시지를 받아서 다른 사용자들에게 보내줌.
        String msg = null;
        ChatThread chatThread = this;
        try {
            while ((msg = in.readLine()) != null) {
                if ("/quit".equalsIgnoreCase(msg)) {
                    System.out.println("채팅을 종료합니다.");
                    break;
                }
                if (msg.indexOf("/to") == 0) {
                    sendMsg(msg);
                } else {
                    broadcast(id + " : " + msg);
                }
                if ("/create".equalsIgnoreCase(msg)) {
                    // 새로운 채팅방을 생성하는 메서드
                    createChat(chatThread);
                }

                if ("/list".equalsIgnoreCase(msg)) {
                    // 리스트를 보여주는 메서드
                    showList();
                }
                if ("/join".equalsIgnoreCase(msg)) {
                    // join 뒤에 숫자 받아 오기
                    // 메시지에서 방 번호를 추출합니다.
                    String[] parts = msg.split(" ");
                    if (parts.length != 2) {
                        System.out.println("잘못된 명령어 형식입니다. 사용법: /join 방번호");
                        return;
                    }
                    int roomId;
                    try {
                        roomId = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 방 번호입니다.");
                        return;
                    }

                    // 방번호의 채팅방으로 입장하는 메서드
                    joinChat(roomId);
                }
                if ("/exit".equalsIgnoreCase(msg)) {
                    //[방번호] 채팅방에서 나가는 메서드
                    String[] parts = msg.split(" ");
                    if (parts.length != 2) {
                        System.out.println("잘못된 명령어 형식입니다. 사용법: /exit 방번호");
                        return;
                    }
                    int roomId;
                    try {
                        roomId = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 방 번호입니다.");
                        return;
                    }

                    exitChat(roomId);
                }

            }

        } catch (IOException e) {
            System.out.println(e);
        }finally {
            synchronized (chatClients) {
                chatClients.remove(id);
            }
            broadcast(id + " 님이 채팅에서 나갔습니다.");

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void exitChat(int roomId) {
        // 1. 채팅방 객체를 찾습니다.
        ChatRoom room = RoomController.getInstance().getRoomList().get(roomId);
        if (room == null) {
            System.out.println("해당 방이 존재하지 않습니다.");
            return;
        }

        // 2. 채팅방에서 사용자를 제거합니다.
        ChatThread currentThread = (ChatThread) Thread.currentThread();
        room.removeChatThread(currentThread);

        // 3. 사용자에게 채팅방에서 나갔음을 알리는 메시지를 보냅니다.
        currentThread.out("채팅방에서 나갔습니다.");

        // 4. 사용자와의 연결을 종료합니다.
        currentThread.closeConnection();

        // 사용자의 소켓과 스트림을 닫습니다.
        try {
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void closeConnection() {
        try {
            socket.close(); // 소켓 연결 종료
        } catch (IOException e) {
            System.err.println("소켓 연결을 닫는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void out(String s) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(s); // 메시지 전송
        } catch (IOException e) {
            System.err.println("메시지를 전송하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void joinChat( int roomId) {

        // RoomController의 인스턴스를 가져옵니다.
        RoomController roomController = RoomController.getInstance();

        // 해당 방번호의 채팅방으로 입장합니다.
        Map<Integer, ChatRoom> roomList = roomController.getRoomList();
        synchronized (roomList) {
            ChatRoom chatRoom = roomList.get(roomId);
            if (chatRoom == null) {
                System.out.println("해당 번호의 채팅방이 존재하지 않습니다.");
                return;
            }

            // 채팅방으로 입장하는 작업을 수행합니다.


            // 2. 채팅방에 새로운 사용자가 입장했음을 다른 사용자에게 알립니다.
            // 입장 메시지를 생성합니다.
            String enterMessage = id + " 님이 채팅방에 입장했습니다.";
            // 채팅방의 모든 사용자에게 입장 메시지를 전송합니다.
            for (ChatThread thread : chatRoom.getChatThreads()) {
                if (thread != this) {
                    thread.sendMsg(enterMessage);
                }
            }
        }
    }

    private void createChat(ChatThread chatThread) {
        // RoomController의 인스턴스를 가져옵니다.
        RoomController roomController = RoomController.getInstance();
        roomController.create(chatThread);

        // 생성된 채팅방 목록을 출력합니다.
        System.out.println("새로운 채팅방이 생성되었습니다.");

        showList();
    }

    private void showList() {
        System.out.println("현재 채팅방 목록:");
        Map<Integer, ChatRoom> roomList = RoomController.getInstance().getRoomList();

        synchronized (roomList) {
            for (ChatRoom room : roomList.values()) {
                System.out.println(room);
            }
        }

//        synchronized (roomList) {
//            for (ChatRoom room : roomList.values()) {
//                System.out.println(room);
//            }
//        }
    }

    //메시지를 특정 사용자에게만 보내는 메서드
    public void sendMsg(String msg) {
        int firstSpaceIndex = msg.indexOf(" ");
        if (firstSpaceIndex == -1) {
            return;  // 공백이 없다면... 실행 종료
        }
        int secontSpaceIndex = msg.indexOf(" ", firstSpaceIndex + 1);
        if (secontSpaceIndex == -1) {
            return;
        }
        String to = msg.substring(firstSpaceIndex + 1, secontSpaceIndex);
        String message = msg.substring(secontSpaceIndex + 1);

        //to(수신자)에게 메시지 전송.
        PrintWriter pw = chatClients.get(to);
        if (pw != null) {
            pw.println(id + "님으로 부터 온 비밀 메시지 " + message);
        } else {
            System.out.println("오류 ;: 수신자 " + to + " 님을 찾을 수 없습니다.");
        }

    }


    // 전체 사용자에게 알려주는 메소드
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
}

