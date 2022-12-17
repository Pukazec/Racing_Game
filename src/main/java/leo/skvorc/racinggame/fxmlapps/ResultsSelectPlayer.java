package leo.skvorc.racinggame.fxmlapps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import kotlin.Result;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.utils.FxmlUtils;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ResultsSelectPlayer implements Initializable {

    @FXML
    private RadioButton rbPlayer1;

    @FXML
    private RadioButton rbPlayer2;

    private ToggleGroup radioButtonGroup1;

    private static String playerName;

    public static String getPlayerName() {
        return playerName;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Config config = SerializerDeserializer.loadConfig();

        radioButtonGroup1 = new ToggleGroup();
        rbPlayer1.setToggleGroup(radioButtonGroup1);
        rbPlayer2.setToggleGroup(radioButtonGroup1);

        rbPlayer1.setText(config.getPlayer1().getPlayerName());
        rbPlayer2.setText(config.getPlayer2().getPlayerName());

        rbPlayer1.setStyle("-fx-background-color: " + config.getPlayer1().getCarColor().toString().toLowerCase());
        rbPlayer2.setStyle("-fx-background-color: " + config.getPlayer2().getCarColor().toString().toLowerCase());
    }

    public void selectedPlayer() throws IOException {
        playerName = ((RadioButton) radioButtonGroup1.getSelectedToggle()).getText();

        FxmlUtils.showScreen("results.fxml", ResultsApplication.getMainStage());
    }
}
