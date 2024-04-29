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

            Map<String, PrintWriter> chatClients = new HashMap<>(); //클라이언트 리스트
            Map<String, Integer> userRooms = new HashMap<>();  //방 리스트

            while(true) {
                Socket socket = serverSocket.accept();
                new ChatThread(socket, chatClients, userRooms).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



