package leo.skvorc.racinggame;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import leo.skvorc.racinggame.model.PlayerDetails;
import org.jetbrains.annotations.NotNull;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RacingFactory implements EntityFactory {

    private final Config config;

    public RacingFactory(Config config) {
        this.config = config;
    }

    @Spawns("player1")
    public Entity newCar1(SpawnData data) {
        return getPlayer(data, config.getPlayer1());
    }

    @Spawns("player2")
    public Entity newCar2(SpawnData data) {
        return getPlayer(data, config.getPlayer2());
    }

    @Spawns("finish")
    public Entity newFinish(SpawnData data){
        return getWall(data, EntityType.FINISH);
    }

    @Spawns("rightWall")
    public Entity newRightWall(SpawnData data){
        return getWall(data, EntityType.RIGHTWALL);
    }

    @Spawns("leftWall")
    public Entity newLeftWall(SpawnData data){
        return getWall(data, EntityType.LEFTWALL);
    }

    @NotNull
    private Entity getPlayer(SpawnData data, PlayerDetails player) {
        return entityBuilder(data)
                .type(EntityType.PLAYER)
                .viewWithBBox("car" + player.getCarColor() + ".png")
                .collidable()
                .buildAndAttach();
    }

    @NotNull
    private static Entity getWall(SpawnData data, EntityType wallType) {
        return entityBuilder(data)
                .type(wallType)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.TRANSPARENT))
                .collidable()
                .build();
    }
}
