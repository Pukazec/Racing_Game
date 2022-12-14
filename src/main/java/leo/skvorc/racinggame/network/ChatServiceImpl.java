package leo.skvorc.racinggame.network;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceImpl implements ChatService {

    List<String> chatHistoryMessageList;

    public ChatServiceImpl() {
        chatHistoryMessageList = new ArrayList<>();
    }

    @Override
    public void sendMessage(String player, String newMessage) throws RemoteException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocalDateTime.now());
        stringBuilder.append(": ");
        stringBuilder.append(player);
        stringBuilder.append(" -> ");
        stringBuilder.append(newMessage);
        chatHistoryMessageList.add(stringBuilder.toString());
    }

    @Override
    public List<String> getChatHistory() throws RemoteException {
        return chatHistoryMessageList;
    }
}
