package leo.skvorc.racinggame.model;

import java.time.Duration;

public class CarCollision {
    private PlayerDetails player;
    private double positionX;
    private double positionY;
    private double rotation;
    private Duration timeStamp;

    public CarCollision() {
    }

    public CarCollision(PlayerDetails player, double positionX, double positionY, double rotation, Duration timeStamp) {
        this.player = player;
        this.positionX = positionX;
        this.positionY = positionY;
        this.rotation = rotation;
        this.timeStamp = timeStamp;
    }

    public PlayerDetails getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDetails player) {
        this.player = player;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Duration getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Duration timeStamp) {
        this.timeStamp = timeStamp;
    }
}
