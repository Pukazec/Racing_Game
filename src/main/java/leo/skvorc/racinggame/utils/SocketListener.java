package leo.skvorc.racinggame.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SocketListener {

    public void acceptRequests(int PORT) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.err.println("Server listening on port:" + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                new Thread(() -> processSerializableClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void processSerializableClient(Socket clientSocket);
}
