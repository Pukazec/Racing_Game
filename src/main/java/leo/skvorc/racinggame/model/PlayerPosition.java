package leo.skvorc.racinggame.model;

import java.io.Serializable;

public class PlayerPosition implements Serializable {
    private Double posX;
    private Double posY;
    private Double rotation;

    public PlayerPosition() {
    }

    public PlayerPosition(Double posX, Double posY, Double rotation) {
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
    }

    public Double getPosX() {
        return posX;
    }

    public void setPosX(Double posX) {
        this.posX = posX;
    }

    public Double getPosY() {
        return posY;
    }

    public void setPosY(Double posY) {
        this.posY = posY;
    }

    public Double getRotation() {
        return rotation;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }
}
