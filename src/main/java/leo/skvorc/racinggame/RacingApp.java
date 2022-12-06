package leo.skvorc.racinggame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.model.PlayerMetaData;
import leo.skvorc.racinggame.model.PlayerPosition;
import leo.skvorc.racinggame.server.Server;
import leo.skvorc.racinggame.utils.MoveDirection;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RacingApp extends GameApplication {

    private final MoveDirection moveDirection = new MoveDirection();

    private Config config;

    private static Map<Long, PlayerMetaData> playersMetadata = new HashMap<>();

    private Entity player1;
    private Entity player2;

    private int lapCounterP1 = 0;
    private int lapCounterP2 = 0;
    private boolean p1LastLap = false;
    private boolean p2LastLap = false;
    private Socket clientSocket;

    public static void main(String[] args) {
        launch(args);
    }

    //region Initialization
    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("Racing game");
        gameSettings.setFullScreenFromStart(true);
        gameSettings.setWidth(15 * 128);
        gameSettings.setHeight(8 * 128);

        initNetwork();
    }

    private void initNetwork() {
        sendPort();
        recivePort();
    }

    private void sendPort() {
        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)){
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            PlayerMetaData newPlayerMetaData = new PlayerMetaData(clientSocket.getLocalAddress().toString().substring(1),
                    clientSocket.getPort(), config,ProcessHandle.current().pid());
            playersMetadata.put(ProcessHandle.current().pid(), newPlayerMetaData);

            oos.writeObject(newPlayerMetaData);

            Integer readObject = (Integer) ois.readObject();
            playersMetadata.get(ProcessHandle.current().pid()).setPort(readObject);
            System.err.println("Player port: " + playersMetadata.get(ProcessHandle.current().pid()).getPort());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void recivePort() {
        try (ServerSocket serverSocket = new ServerSocket(playersMetadata.get(ProcessHandle.current().pid()).getPort())) {
            System.err.println("Server listening on port:" + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                new Thread(() -> loadPort(clientSocket)).start();
                serverSocket.close();
            }

        } catch (SocketException se){
            System.err.println("Socket closed");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPort(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())){
            PlayerMetaData answer = (PlayerMetaData) ois.readObject();
            //System.err.println(answer);

            playersMetadata.put(answer.getPid(), answer);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> moveDirection.MoveForward(player1));
        onKey(KeyCode.S, () -> moveDirection.MoveBackwards(player1));
        onKey(KeyCode.A, () -> moveDirection.TurnLeft(player1, 1));
        onKey(KeyCode.D, () -> moveDirection.TurnRight(player1, 1));
        onKey(KeyCode.UP, () -> moveDirection.MoveForward(player2));
        onKey(KeyCode.DOWN, () -> moveDirection.MoveBackwards(player2));
        onKey(KeyCode.LEFT, () -> moveDirection.TurnLeft(player2, 1));
        onKey(KeyCode.RIGHT, () -> moveDirection.TurnRight(player2, 1));
    }

    //endregion
    @Override
    protected void initGame() throws RuntimeException {

        config = SerializerDeserializer.loadConfig();

        getGameWorld().addEntityFactory(new RacingFactory(config));

        setLevelFromMap("tmx/track" + config.getTrack() + ".tmx");

        player1 = spawn("player1", 1050, 765);
        player2 = spawn("player2", 1050, 805);
        player1.rotateBy(-90);
        player2.rotateBy(-90);

        loopBGM("avalanche.mp3");
        new Thread(() -> networkListener()).start();
        //new Thread(() -> setupSocket()).start();
        initSendingSocket();
    }

    private void initSendingSocket() {
        Long pidSecondPlayer = playersMetadata.keySet().stream().filter(p -> !p.equals(ProcessHandle.current().pid())).findFirst().get();
        PlayerMetaData secondPlayerMetaData = playersMetadata.get(pidSecondPlayer);
        try {
            clientSocket = new Socket(Server.HOST, secondPlayerMetaData.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //region Physics
    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.PLAYER, EntityType.FINISH, (car, finish) -> {

            p1LastLap = checkLastLap(lapCounterP1);
            p2LastLap = checkLastLap(lapCounterP2);

            checkWin(lapCounterP1, p1LastLap, config.getPlayer1());
            checkWin(lapCounterP2, p2LastLap, config.getPlayer2());

            if (car.equals(player1)) {
                lapCounterP1++;
            }

            if (car.equals(player2)) {
                lapCounterP2++;
            }
        });

        onCollisionBegin(EntityType.PLAYER, EntityType.RIGHTWALL, (car, wall) -> moveDirection.RightCollision(car));

        onCollisionBegin(EntityType.PLAYER, EntityType.LEFTWALL, (car, wall) -> moveDirection.LeftCollision(car));
    }

    private boolean checkLastLap(int lapCounter) {
        return lapCounter >= config.getNumLaps();
    }

    private void checkWin(int lapCounter, boolean lastLap, PlayerDetails player) {
        if (lapCounter == config.getNumLaps() + 1 && lastLap) {
            showMessage("Player " + player.getPlayerName() + " won!", () -> {
                lapCounterP1 = 0;
                lapCounterP2 = 0;
                player.recordWin();
                newGame();
            });
        }
    }

    private void newGame() {
        SerializerDeserializer.saveConfig(config);
        getGameController().startNewGame();
    }
    //endregion

    @Override
    protected void onUpdate(double tpf) {
        System.err.println("Send Update");
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());

            PlayerPosition playerPosition = new PlayerPosition(player1.getX(),player1.getY(),player1.getRotation());

            oos.writeObject(playerPosition);
        } catch (IOException e) {
            System.err.println("Error sending data");;
        }
    }

    //region Network

    //private void setupSocket() { }
    private void networkListener() {
        try (ServerSocket serverSocket = new ServerSocket(playersMetadata.get(ProcessHandle.current().pid()).getPort())) {
            System.err.println("Server listening for updates on port:" + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Racing game connected from port: " + clientSocket.getPort());

                new Thread(() -> processSerializableClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())){
            PlayerPosition answer = (PlayerPosition) ois.readObject();
            player2.setPosition(answer.getPosX(), answer.getPosY());
            player2.setRotation(answer.getRotation());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //endregion
}
