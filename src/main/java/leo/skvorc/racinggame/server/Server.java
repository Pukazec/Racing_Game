package leo.skvorc.racinggame.server;

import leo.skvorc.racinggame.model.PlayerMetaData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static final String HOST = "localhost";
    public static final int PORT = 850;

    private static Map<Long, PlayerMetaData> players = new HashMap<>();

    public static void main(String[] args) {
        acceptRequests();
    }

    private static void acceptRequests() {
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

    private static void processSerializableClient(Socket clientSocket) {
        System.err.println("Processing...");

        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())){

            PlayerMetaData playerMetaData = (PlayerMetaData) ois.readObject();
            System.out.println("Connected player metadata: " +
                    playerMetaData.getPlayerName() + " " +
                    playerMetaData.getIpAddress() + " " +
                    playerMetaData.getPort() + " " +
                    playerMetaData.getPid());

            oos.writeObject(clientSocket.getPort());

            System.out.println("Object sent");
            /*if(players.size() < 2) {
                System.out.println("Adding a new player to the game!");
                players.put(playerMetaData.getPid(), playerMetaData);
                oos.writeObject("SUCCESS");
            }
            else {
                //message sent to the second client
                oos.writeObject("ERROR");
            }

            if(players.size() == 2) {
                System.out.println("Two players joined!");
                //message sent to the second client
                oos.writeObject("SUCCESS");

                Long pidFirstPlayer =
                        players.keySet().stream().filter(p -> !p.equals(playerMetaData.getPid())).findFirst().get();

                PlayerMetaData firstPlayerMetaData = players.get(pidFirstPlayer);

                Socket firstClientSocket = new Socket(firstPlayerMetaData.getIpAddress(),
                        Integer.parseInt(firstPlayerMetaData.getPort()));
                ObjectOutputStream oosFirstClient = new ObjectOutputStream(firstClientSocket.getOutputStream());
                ObjectInputStream oisFirstClient = new ObjectInputStream(firstClientSocket.getInputStream());
                System.err.println("Client is connecting to " + firstClientSocket.getInetAddress() + ":" +firstClientSocket.getPort());
                oos.writeObject("SUCCESS");
            }*/

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}