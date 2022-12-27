package leo.skvorc.racinggame.network;

import leo.skvorc.racinggame.utils.JndiUtils;

import javax.naming.NamingException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer {
    public static void main(String[] args) {
        try {
            String rmiPortString = JndiUtils.getConfigurationParameter("rmi.port");
            String randomPortHintString = JndiUtils.getConfigurationParameter("rmi.randomPortHint");
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(rmiPortString));
            ChatService chatService = new ChatServiceImpl();
            ChatService skeleton = (ChatService) UnicastRemoteObject.exportObject(chatService, Integer.parseInt(randomPortHintString));
            registry.rebind(ChatService.REMOTE_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (NamingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
