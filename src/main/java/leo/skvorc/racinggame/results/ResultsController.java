package leo.skvorc.racinggame.results;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.utils.ImageLoader;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {

    private Config config;

    @FXML
    private Label lblPlayer1Name;

    @FXML
    private Label lblPlayer1NumOfWins;

    @FXML
    private Label lblPlayer2Name;
    @FXML
    private Label lblPlayer2NumOfWins;

    @FXML
    private Label lblNumOfLaps;
    @FXML
    private ImageView imgTrack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        config = SerializerDeserializer.loadConfig();

        lblPlayer1Name.setText(config.getPlayer1().getPlayerName());
        lblPlayer1NumOfWins.setText(String.valueOf(config.getPlayer1().getNumberOfWins()));
        lblPlayer2Name.setText(config.getPlayer2().getPlayerName());
        lblPlayer2NumOfWins.setText(String.valueOf(config.getPlayer2().getNumberOfWins()));
        lblNumOfLaps.setText(String.valueOf(config.getNumLaps()));
        if (config.getTrack() == 1){ imgTrack.setImage(ImageLoader.loadImage(Config.TRACK1)); }
        if (config.getTrack() == 2){ imgTrack.setImage(ImageLoader.loadImage(Config.TRACK2)); }
    }
}
