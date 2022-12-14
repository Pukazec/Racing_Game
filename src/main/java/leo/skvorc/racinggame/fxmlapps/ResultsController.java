package leo.skvorc.racinggame.fxmlapps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.utils.DocumentationGenerator;
import leo.skvorc.racinggame.utils.ImageLoader;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {

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
    @FXML
    private Button btnDocumentation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Config config = SerializerDeserializer.loadConfig();

        lblPlayer1Name.setText(config.getPlayer1().getPlayerName());
        lblPlayer1NumOfWins.setText(String.valueOf(config.getPlayer1().getNumberOfWins()));
        lblPlayer2Name.setText(config.getPlayer2().getPlayerName());
        lblPlayer2NumOfWins.setText(String.valueOf(config.getPlayer2().getNumberOfWins()));
        lblNumOfLaps.setText(String.valueOf(config.getNumLaps()));
        if (config.getTrack() == 1){ imgTrack.setImage(ImageLoader.loadImage(Config.TRACK1)); }
        if (config.getTrack() == 2){ imgTrack.setImage(ImageLoader.loadImage(Config.TRACK2)); }
    }

    public void documentation(){
        DocumentationGenerator.generateParametersString();
        btnDocumentation.setText("Documentation generated");
        btnDocumentation.setDisable(true);
    }

}
