package Ex;


import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ChatServer {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(12345);) {
            System.out.println("서버 접속 완료");
            Map<String, PrintWriter> chatClients = new HashMap<>(); //유저 리스트
            Map<Integer, List<PrintWriter>> rooms = new HashMap<>(); //방 리스트

            while(true) {
                Socket socket = serverSocket.accept();
                new ChatThread(socket, chatClients, rooms).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}