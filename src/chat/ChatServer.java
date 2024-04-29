package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class ChatServer {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(12345);) {
            System.out.println("서버 접속 완료");
            Map<String, PrintWriter> chatClients = new HashMap<>();

            while(true) {
                Socket socket = serverSocket.accept();
                new ChatThread(socket, chatClients).start();
            }

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

class ChatThread extends Thread {
    private Socket socket;
    private String nickname;
    private BufferedReader in;
    private Map<String, PrintWriter> chatClients;
    PrintWriter out;
    List<ChatThread> list;

    public ChatThread(Socket socket, Map<String, PrintWriter> chatClients) {
        this.socket = socket;
        this.chatClients = chatClients;

        //클라이언트 생성하면서 닉네임 받아오기, ip주소 출력
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            nickname = in.readLine(); //닉네임 입력

            //채팅 동시 처리하기
            synchronized (chatClients) {
                chatClients.put(this.nickname, out);
            }
            brodadcast(nickname + "닉네임의 사용자가 연결했습니다.");
            System.out.println(nickname + "님 IP 주소는 : " + socket.getInetAddress());


        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        System.out.println(nickname + "님이 채팅을 시작합니다.");
        String msg;

        try {
            while ((msg = in.readLine()) != null) {
                // /bye -> 종료하기
                if ("/bye".equalsIgnoreCase(msg))
                    break;
                brodadcast(nickname + " : " + msg);

                // /list -> 방 목록 보기
                if ("/bye".equalsIgnoreCase(msg));

                // /create -> 방 생성
                if ("/create".equalsIgnoreCase(msg)); {
                    RoomController roomController = null;
                    roomController.create();
                }

                // /join [방번호] -> 방 입장
                if ("/join".equalsIgnoreCase(msg));

                // /exit -> 방 나가기
                if ("/exit".equalsIgnoreCase(msg));

                // /bye -> 접속종료

            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            brodadcast(nickname + "닉네임의 사용자가 연결을 끊었습니다.");
            synchronized (chatClients) {
                chatClients.remove(nickname);
            }
        }
        if(in != null) {
            try{
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void brodadcast(String msg) {
        //List<ChatThread> chatThreadList = new ArrayList<>();
        //Collections.copy(chatThreadList, this.list);
        synchronized (chatClients) {
            Iterator<PrintWriter> it = chatClients.values().iterator();
            while(it.hasNext()) {
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



