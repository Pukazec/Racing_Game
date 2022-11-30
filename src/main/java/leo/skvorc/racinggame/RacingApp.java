package leo.skvorc.racinggame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.utils.MoveDirection;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RacingApp extends GameApplication implements Runnable{

    private final MoveDirection moveDirection = new MoveDirection();

    private Config config;

    private Entity player1;
    private Entity player2;

    private int lapCounterP1 = 0;
    private int lapCounterP2 = 0;
    private boolean p1LastLap = false;
    private boolean p2LastLap = false;

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

        config = SerializerDeserializer.loadConfig();

        getGameWorld().addEntityFactory(new RacingFactory(config));

        setLevelFromMap("tmx/track" + config.getTrack() + ".tmx");

        player1 = spawn("player1", 1050, 765);
        player2 = spawn("player2", 1050, 805);
        player1.rotateBy(-90);
        player2.rotateBy(-90);

        loopBGM("avalanche.mp3");
    }

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

    @Override
    public void run() {
        System.out.println("hello from racing app");
        main(new String[]{""});
    }
}
