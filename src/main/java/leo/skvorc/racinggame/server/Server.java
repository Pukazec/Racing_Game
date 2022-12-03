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

            players.put(playerMetaData.getPid(), playerMetaData);
            System.out.println(playerMetaData.getPort());
            oos.writeObject(clientSocket.getPort());

            System.out.println("Object sent");

            if(players.size() == 2) {
                startGame(playerMetaData.getPid());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void startGame(Long pid) throws IOException {
        System.out.println("Two players joined!");

        Long pidFirstPlayer = players.keySet().stream().filter(p -> !p.equals(pid)).findFirst().get();
        Long pidSecondPlayer = players.keySet().stream().filter(p -> p.equals(pid)).findFirst().get();

        PlayerMetaData firstPlayerMetaData = players.get(pidFirstPlayer);
        PlayerMetaData secondPlayerMetaData = players.get(pidSecondPlayer);

        sendStart(firstPlayerMetaData.getIpAddress(), Integer.parseInt(firstPlayerMetaData.getPort()));
        sendStart(secondPlayerMetaData.getIpAddress(), Integer.parseInt(secondPlayerMetaData.getPort()));

    }

    private static void sendStart(String ipAddress, int port) throws IOException {
        Socket firstClientSocket = new Socket(ipAddress,port);
        ObjectOutputStream oosFirstClient = new ObjectOutputStream(firstClientSocket.getOutputStream());
        System.err.println("Client is connecting to " + firstClientSocket.getInetAddress() + ":" +firstClientSocket.getPort());
        oosFirstClient.writeObject("START");
    }

}
