package leo.skvorc.racinggame;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RacingFactory implements EntityFactory {

    private Config config;

    public RacingFactory(Config config) {
        this.config = config;
    }

    @Spawns("player1")
    public Entity newCar1(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PLAYER)
                .viewWithBBox("car" + config.getPlayer1().getCarColor() + ".png")
                .collidable()
                .buildAndAttach();
    }

    @Spawns("player2")
    public Entity newCar2(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PLAYER)
                .viewWithBBox("car" + config.getPlayer2().getCarColor() + ".png")
                .collidable()
                .buildAndAttach();
    }

    @Spawns("finish")
    public Entity newFinish(SpawnData data){
        return entityBuilder()
                .from(data)
                .type(EntityType.FINISH)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.RED))
                .collidable()
                .build();
    }

    @Spawns("rightWall")
    public Entity newRightWall(SpawnData data){

        return entityBuilder()
                .from(data)
                .type(EntityType.RIGHTWALL)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.TRANSPARENT))
                .collidable()
                .build();
    }

    @Spawns("leftWall")
    public Entity newLeftWall(SpawnData data){

        return entityBuilder()
                .from(data)
                .type(EntityType.LEFTWALL)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.TRANSPARENT))
                .collidable()
                .build();
    }
}
