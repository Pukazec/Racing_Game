package leo.skvorc.racinggame.model;

import java.io.Serializable;

public class Config implements Serializable {

    public static final int BLOCK_SIZE = 128;
    public static final String TRACK1 = "src/main/resources/leo/skvorc/racinggame/images/track1.png";
    public static final String TRACK2 = "src/main/resources/leo/skvorc/racinggame/images/track2.png";

    private PlayerDetails player1;

    private PlayerDetails player2;

    private int track;

    private int numLaps;

    public Config(PlayerDetails player1, PlayerDetails player2, int track, int numLaps) {
        this.player1 = player1;
        this.player2 = player2;
        this.track = track;
        this.numLaps = numLaps;
    }

    public Config(PlayerDetails player1, PlayerDetails player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public PlayerDetails getPlayer1() { return player1; }

    public PlayerDetails getPlayer2() { return player2; }

    public void setPlayer1(PlayerDetails player1) { this.player1 = player1; }

    public void setPlayer2(PlayerDetails player2) { this.player2 = player2; }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public int getNumLaps() { return numLaps; }

    public void setNumLaps(int numLaps) {
        this.numLaps = numLaps;
    }
}
