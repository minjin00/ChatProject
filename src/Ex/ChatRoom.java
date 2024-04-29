package Ex;

import chat.ChatClient;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom {
    private int ROOMID; //채팅방의 ID
    private List<ChatThread> chatThreads;
//    private List<ChatClient> clients; //채팅방 참여 유저
//    private Map<String, PrintWriter> chatClients = new HashMap<>();
//    public static List<ChatRoom> chatRooms = new ArrayList<>(); //채팅방 관리하는 리스트

    public ChatRoom(String ROOMID) {
        this.ROOMID = Integer.parseInt(ROOMID);
        this.chatThreads = new ArrayList<>();
    }

    public int getROOMID() {
        return ROOMID;
    }

    @Override
    public String toString() {
        return "방 번호 : " + ROOMID + ", 참여자 수 : " + chatThreads.size();
    }

    public void addChatThread(ChatThread chatThread) {
        chatThreads.add(chatThread);
    }

    public void removeChatThread(ChatThread chatThread) {
        chatThreads.remove(chatThread);
    }

    public List<ChatThread> getChatThreads() {
        return chatThreads;
    }
}
