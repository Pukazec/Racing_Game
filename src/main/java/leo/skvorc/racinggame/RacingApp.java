package leo.skvorc.racinggame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import leo.skvorc.racinggame.model.CarCollision;
import leo.skvorc.racinggame.model.Config;
import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.utils.MoveDirection;
import leo.skvorc.racinggame.utils.SerializerDeserializer;
import leo.skvorc.racinggame.utils.XMLUtils;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RacingApp extends GameApplication {

    private static final String COLLISIONS_FILE = "collisions.txt";

    private final MoveDirection moveDirection = new MoveDirection();

    private Config config;

    private Entity player1;
    private Entity player2;

    private int lapCounterP1 = 0;
    private int lapCounterP2 = 0;
    private boolean p1LastLap = false;
    private boolean p2LastLap = false;

    private List<CarCollision> collisionList;
    private LocalDateTime startTime;
    private boolean threadInUse = false;
    private boolean replay = true;
    private Duration lastDuration;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("Racing game");
        gameSettings.setFullScreenFromStart(true);
        gameSettings.setWidth(15 * 128);
        gameSettings.setHeight(8 * 128);
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

    @Override
    protected void initGame() {
        collisionList = new ArrayList<>();

        config = SerializerDeserializer.loadConfig();

        getGameWorld().addEntityFactory(new RacingFactory(config));

        setLevelFromMap("tmx/track" + config.getTrack() + ".tmx");

        player1 = spawn("player1", 1050, 765);
        player2 = spawn("player2", 1050, 805);
        player1.rotateBy(-90);
        player2.rotateBy(-90);

        //loopBGM("avalanche.mp3");

        if (replay) {
            collisionList = XMLUtils.readXML();
        }

        startTime = LocalDateTime.now();
        lastDuration = Duration.between(startTime, LocalDateTime.now());
        new Thread(() -> {
            while (true) {
                if (collisionList.size() != 0) {
                    new Thread(this::calculateCollisions).start();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    protected void initPhysics() {
        if (!replay) {
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

            onCollisionBegin(EntityType.PLAYER, EntityType.RIGHTWALL, (car, wall) -> {
                new Thread(() -> recordCollision(car)).start();
                moveDirection.RightCollision(car);
            });

            onCollisionBegin(EntityType.PLAYER, EntityType.LEFTWALL, (car, wall) -> {
                new Thread(() -> recordCollision(car)).start();
                moveDirection.LeftCollision(car);
            });
        }
    }

    private boolean checkLastLap(int lapCounter) {
        return lapCounter >= config.getNumLaps();
    }

    private void checkWin(int lapCounter, boolean lastLap, PlayerDetails player) {
        if (lapCounter == config.getNumLaps() + 1 && lastLap) {
            XMLUtils.saveXml(collisionList);
            showMessage("Player " + player.getPlayerName() + " won!", () -> {
                lapCounterP1 = 0;
                lapCounterP2 = 0;
                player.recordWin();
                newGame();
            });
        }
    }

    @Override
    protected void initUI() {
        Text playerOneHits = new Text(config.getPlayer1().getPlayerName() + ": 0 hits");
        playerOneHits.setTranslateX(10);
        playerOneHits.setTranslateY(50);
        playerOneHits.setFont(Font.font(45));
        getGameScene().addUINode(playerOneHits);

        Text playerTwoHits = new Text(config.getPlayer2().getPlayerName() + ": 0 hits");
        playerTwoHits.setTranslateX(10);
        playerTwoHits.setTranslateY(100);
        playerTwoHits.setFont(Font.font(45));
        getGameScene().addUINode(playerTwoHits);
    }

    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);
        if (replay){
            for (CarCollision collision : collisionList) {
                calculatePassedTime(collision);
            }
        }
    }

    private void calculatePassedTime(CarCollision collision) {
        Duration passed = Duration.between(startTime, LocalDateTime.now());
        Duration timeStamp = collision.getTimeStamp();
        if (timeStamp.toMillis() > lastDuration.toMillis() && timeStamp.toMillis() < passed.toMillis()){
            if (collision.getPlayer() == config.getPlayer1().getPlayerName()) {
                updatePosition(player1, collision);
            } else {
                updatePosition(player2, collision);
            }
        }
    }

    private void updatePosition(Entity player, CarCollision collision) {
        player.setPosition(collision.getPositionX(), collision.getPositionY());
        player.setRotation(collision.getRotation());
    }

    private void newGame() {
        SerializerDeserializer.saveConfig(config);
        replay = true;
        getGameController().startNewGame();
    }

    private synchronized void recordCollision(Entity car) {
        while (threadInUse) {
            try {
                System.out.println("Thread for recording cannot start");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        threadInUse = true;

        PlayerDetails playerDetails = car == player1 ? config.getPlayer1() : config.getPlayer2();
        Duration timeStamp = Duration.between(startTime, LocalDateTime.now());
        collisionList.add(new CarCollision(playerDetails.getPlayerName(), car.getX(), car.getY(), car.getRotation(), timeStamp));

        try (FileWriter fileWriter = new FileWriter(COLLISIONS_FILE)) {
            StringBuilder stringBuilder = new StringBuilder();
            collisionList.forEach(c -> {
                stringBuilder.append(c.getPlayer());
                stringBuilder.append(',');
                stringBuilder.append(c.getPositionX());
                stringBuilder.append(',');
                stringBuilder.append(c.getPositionY());
                stringBuilder.append(',');
                stringBuilder.append(c.getRotation());
                stringBuilder.append(',');
                stringBuilder.append(c.getTimeStamp());
                stringBuilder.append(',');
                stringBuilder.append(System.lineSeparator());
            });
            fileWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        threadInUse = false;
        notifyAll();
    }

    private synchronized void calculateCollisions() {
        while (threadInUse) {
            try {
                System.out.println("Thread for calculation cannot start");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        threadInUse = true;

        AtomicInteger player1Collisions = new AtomicInteger();
        AtomicInteger player2Collisions = new AtomicInteger();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(COLLISIONS_FILE))) {
            bufferedReader.lines().forEach(line -> {
                String[] split = line.split(",");
                String name = split[0];
                if (name.equals(config.getPlayer1().getPlayerName())) {
                    player1Collisions.getAndIncrement();
                } else {
                    player2Collisions.getAndIncrement();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Text label = (Text) getGameScene().getUINodes().get(0);
        label.setText(config.getPlayer1().getPlayerName() + ": " + player1Collisions.get() + " hits");

        label = (Text) getGameScene().getUINodes().get(1);
        label.setText(config.getPlayer2().getPlayerName() + ": " + player2Collisions.get() + " hits");

        threadInUse = false;
        notifyAll();
    }

}
