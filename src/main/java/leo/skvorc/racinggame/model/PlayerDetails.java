package leo.skvorc.racinggame.model;

import java.io.Serializable;

public class PlayerDetails implements Serializable {

    private String playerName;
    private CarColor carColor;

    private int numberOfWins;

    public PlayerDetails(String playerName, CarColor carColor, int numberOfWins) {
        this.playerName = playerName;
        this.carColor = carColor;
        this.numberOfWins = numberOfWins;
    }

    public PlayerDetails(String playerName, CarColor carColor) {
        this.playerName = playerName;
        this.carColor = carColor;
    }

    public String getPlayerName() {
        return playerName;
    }

    public CarColor getCarColor() {
        return carColor;
    }

    public void recordWin() { numberOfWins++; }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setCarColor(CarColor carColor) {
        this.carColor = carColor;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }
}
