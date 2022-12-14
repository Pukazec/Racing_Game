package leo.skvorc.racinggame.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {

    String REMOTE_OBJECT_NAME = "leo.skvorc.rmi.services";

    void sendMessage(String player, String newMessage) throws RemoteException;

    List<String> getChatHistory() throws RemoteException;
}
