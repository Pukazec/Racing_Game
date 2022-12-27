package leo.skvorc.racinggame.network;

import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.model.PlayerMetaData;
import leo.skvorc.racinggame.utils.JndiUtils;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static int PORT;

    private static Map<Long, PlayerMetaData> players = new HashMap<>();
    private static Map<Long, PlayerMetaData> playerPorts = new HashMap<>();

    private static int connectionNumber = 0;

    public static void main(String[] args) {
        acceptRequests();
    }

    private static void acceptRequests() {
        try {
            String serverPortString = JndiUtils.getConfigurationParameter("server.port");
            PORT = Integer.parseInt(serverPortString);
        } catch (NamingException | IOException e) {
            throw new RuntimeException(e);
        }

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
                if (connectionNumber >= 3 && connectionNumber < 5) {
                    playerPorts.put(playerMetaData.getPid(), playerMetaData);
                    if (connectionNumber == 4/*TODO*/) {
                        initPorts(playerMetaData.getPid());
                    }
                }
            if (connectionNumber >= 5) {
                recordWin(playerMetaData);
                oos.writeObject("Recording win");
            }
            //endregion


            //region Finish
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
        try {
            Socket firstClientSocket = new Socket(playerMetaData.getIpAddress(), playerMetaData.getPort());
            ObjectOutputStream oosFirstClient = new ObjectOutputStream(firstClientSocket.getOutputStream());
            System.err.println("Client is connecting to " + firstClientSocket.getInetAddress() + ":" + firstClientSocket.getPort());
            oosFirstClient.writeObject(sendData);
        } catch (ConnectException ce) {
            sendPorts(playerMetaData, sendData);
        }
    }


    private static void sendStart(String ipAddress, int port) throws IOException {
        Socket firstClientSocket = new Socket(ipAddress, port);
        ObjectOutputStream oosFirstClient = new ObjectOutputStream(firstClientSocket.getOutputStream());
        System.err.println("Client is connecting to " + firstClientSocket.getInetAddress() + ":" + firstClientSocket.getPort());
        oosFirstClient.writeObject("START");
    }
    //endregion

    //region Finish
    private static void recordWin(PlayerMetaData player) throws IOException {
        System.out.println("Finnish game!");
        Config config = playerPorts.get(player.getPid()).getConfig();
        if (player.getPlayerName() == config.getPlayer1().getPlayerName()) {
            config.getPlayer1().recordWin();
        } else {
            config.getPlayer2().recordWin();
        }
        SerializerDeserializer.saveConfig(playerPorts.get(player.getPid()).getConfig());

        Long pidFirstPlayer = playerPorts.keySet().stream().filter(p -> !p.equals(player.getPid())).findFirst().get();
        Long pidSecondPlayer = playerPorts.keySet().stream().filter(p -> p.equals(player.getPid())).findFirst().get();

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
