package leo.skvorc.racinggame.utils;

import com.almasb.fxgl.entity.Entity;

public class MoveDirection {

    private final int SPEED = 4;
    private final int ROTATION = 4;

    private double getUpMovement(double angel) { return Math.sin(Math.toRadians(angel)); }

    private double getRightMovement(double angel){
        return Math.cos(Math.toRadians(angel));
    }


    public void MoveForward(Entity player) {
        double upMovement = getUpMovement(player.angleProperty().doubleValue() - 90);
        double rightMovement = getRightMovement(player.angleProperty().doubleValue() - 90);
        player.translateY(upMovement * SPEED);
        player.translateX(rightMovement * SPEED);
    }

    public void MoveBackwards(Entity player) {
        double upMovement = getUpMovement(player.angleProperty().doubleValue() + 90);
        double rightMovement = getRightMovement(player.angleProperty().doubleValue() + 90);
        player.translateY(upMovement * SPEED / 2);
        player.translateX(rightMovement * SPEED / 2);
    }

    public void LeftCollision(Entity player){
        double upMovement = - getUpMovement(player.angleProperty().doubleValue());
        double rightMovement = - getRightMovement(player.angleProperty().doubleValue());

        double angle = Math.atan2(upMovement, rightMovement);
        player.setRotation(Math.toDegrees(angle) - 90);
    }

    public void RightCollision(Entity player){
        double upMovement = - getUpMovement(player.angleProperty().doubleValue());
        double rightMovement = - getRightMovement(player.angleProperty().doubleValue());

        double angle = Math.atan2(upMovement, rightMovement);
        player.setRotation(Math.toDegrees(angle) + 90);
    }

    public void TurnRight(Entity player, int rotate){
            player.rotateBy(rotate * ROTATION);
    }

    public void TurnLeft(Entity player, int rotate) { player.rotateBy(-(rotate * ROTATION)); }
}
