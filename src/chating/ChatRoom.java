package chating;

import chating.server.ChatThread;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    //채팅 방
    private int id;
    private List<ChatThread> chatThreads;

    public ChatRoom(int id) {
        this.id = id;
        this.chatThreads = new ArrayList<>();
    }

    // 채팅방 ID 반환
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "방 번호: " + id + ", 참여자 수: " + chatThreads.size();
    }

    // 채팅방에 쓰레드 추가
    public void addChatThread(ChatThread chatThread) {
        chatThreads.add(chatThread);
    }

    // 채팅방에서 쓰레드 제거
    public void removeChatThread(ChatThread chatThread) {
        chatThreads.remove(chatThread);
    }

    // 채팅방에 참여한 쓰레드 목록 반환
    public List<ChatThread> getChatThreads() {
        return chatThreads;
    }

}