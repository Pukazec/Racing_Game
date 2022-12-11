package leo.skvorc.racinggame.server;

import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.model.PlayerMetaData;
import leo.skvorc.racinggame.model.PlayerPosition;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

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
    private static Map<Long, PlayerMetaData> playerPorts = new HashMap<>();

    private static int connectionNumber = 0;

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
        connectionNumber++;
        System.err.println(connectionNumber);

        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

            //region Init Racing
            if (connectionNumber <= 4/*TODO*/) {
                PlayerMetaData playerMetaData = (PlayerMetaData) ois.readObject();
                playerMetaData.setPort(clientSocket.getPort());
                System.out.println("Connected player metadata: " +
                        playerMetaData.getPlayerName() + " " +
                        playerMetaData.getIpAddress() + " " +
                        playerMetaData.getPort() + " " +
                        playerMetaData.getPid());

                players.put(playerMetaData.getPid(), playerMetaData);
                System.out.println(playerMetaData.getPort());
                oos.writeObject(clientSocket.getPort());

                System.out.println("Object sent");

                if (connectionNumber == 2) {
                    startGame(playerMetaData.getPid());
                    writeConfig(playerMetaData.getPid(), playerMetaData.getConfig().getPlayer1());
                }
                if (connectionNumber >= 3) {
                    playerPorts.put(playerMetaData.getPid(), playerMetaData);
                    if (connectionNumber == 4/*TODO*/) {
                        initPorts(playerMetaData.getPid());
                    }
                }
            }
            //endregion


            //region Finish racing
            if (connectionNumber >= 5) {
                Long playerPid = (Long) ois.readObject();
                oos.writeObject("Recording win");
                recordWin(playerPid);
            }
            //endregion
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //region StartApplication
    private static void startGame(Long pid) throws IOException {
        System.out.println("Two players joined!");

        Long pidFirstPlayer = players.keySet().stream().filter(p -> !p.equals(pid)).findFirst().get();
        Long pidSecondPlayer = players.keySet().stream().filter(p -> p.equals(pid)).findFirst().get();

        PlayerMetaData firstPlayerMetaData = players.get(pidFirstPlayer);
        PlayerMetaData secondPlayerMetaData = players.get(pidSecondPlayer);

        sendStart(firstPlayerMetaData.getIpAddress(), firstPlayerMetaData.getPort());
        sendStart(secondPlayerMetaData.getIpAddress(), secondPlayerMetaData.getPort());
    }

    private static void writeConfig(Long pid, PlayerDetails player) {
        Long pidPlayer = players.keySet().stream().filter(p -> !p.equals(pid)).findFirst().get();
        PlayerMetaData playerMetaData = players.get(pidPlayer);
        playerMetaData.getConfig().setPlayer2(player);
        SerializerDeserializer.saveConfig(playerMetaData.getConfig());
    }
    //endregion

    //region Racing start
    private static void initPorts(Long pid) throws IOException {
        System.out.println("Two players start!");

        Long pidFirstPlayer = playerPorts.keySet().stream().filter(p -> !p.equals(pid)).findFirst().get();
        Long pidSecondPlayer = playerPorts.keySet().stream().filter(p -> p.equals(pid)).findFirst().get();

        PlayerMetaData firstPlayerMetaData = playerPorts.get(pidFirstPlayer);
        PlayerMetaData secondPlayerMetaData = playerPorts.get(pidSecondPlayer);

        sendPorts(firstPlayerMetaData, secondPlayerMetaData);
        sendPorts(secondPlayerMetaData, firstPlayerMetaData);
    }

    private static void sendPorts(PlayerMetaData playerMetaData, PlayerMetaData sendData) throws IOException {
        Socket firstClientSocket = new Socket(playerMetaData.getIpAddress(), playerMetaData.getPort());
        ObjectOutputStream oosFirstClient = new ObjectOutputStream(firstClientSocket.getOutputStream());
        System.err.println("Client is connecting to " + firstClientSocket.getInetAddress() + ":" + firstClientSocket.getPort());
        oosFirstClient.writeObject(sendData);
    }


    private static void sendStart(String ipAddress, int port) throws IOException {
        Socket firstClientSocket = new Socket(ipAddress, port);
        ObjectOutputStream oosFirstClient = new ObjectOutputStream(firstClientSocket.getOutputStream());
        System.err.println("Client is connecting to " + firstClientSocket.getInetAddress() + ":" + firstClientSocket.getPort());
        oosFirstClient.writeObject("START");
    }
    //endregion

    //region Finish
    private static void recordWin(Long playerPid) throws IOException {
        System.out.println("Finnish game!");
        players.get(playerPid).getConfig().getPlayer1().recordWin();

        Long pidFirstPlayer = playerPorts.keySet().stream().filter(p -> !p.equals(playerPid)).findFirst().get();
        Long pidSecondPlayer = playerPorts.keySet().stream().filter(p -> p.equals(playerPid)).findFirst().get();

        PlayerMetaData firstPlayerMetaData = playerPorts.get(pidFirstPlayer);
        PlayerMetaData secondPlayerMetaData = playerPorts.get(pidSecondPlayer);

        sendFinish("Finish", firstPlayerMetaData);
        sendFinish("Finish", secondPlayerMetaData);
    }

    private static void sendFinish(String finish, PlayerMetaData playerMetaData) throws IOException {
        Integer port = playerMetaData.getPort();
        port++;
        port++;
        try (Socket clientSocket = new Socket(playerMetaData.getIpAddress(), port)) {
            ObjectOutputStream oosFirstClient = new ObjectOutputStream(clientSocket.getOutputStream());

            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            oosFirstClient.writeObject(finish);
        } catch (IOException e) {
            System.err.println("Error sending data");
        }
    }
    //endregion

}
