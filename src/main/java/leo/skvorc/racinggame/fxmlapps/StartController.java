package leo.skvorc.racinggame.fxmlapps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.model.CarColor;
import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.utils.FxmlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    @FXML
    private TextField txtP1;
    @FXML
    private RadioButton black1;
    @FXML
    private RadioButton orange1;
    @FXML
    private RadioButton green1;
    @FXML
    private RadioButton yellow1;
    @FXML
    private TextField txtP2;
    @FXML
    private RadioButton black2;
    @FXML
    private RadioButton orange2;
    @FXML
    private RadioButton green2;
    @FXML
    private RadioButton yellow2;
    @FXML
    private Label lblError;
    private ToggleGroup radioButtonGroup1;
    private ToggleGroup radioButtonGroup2;

    private static Config config;

    private final List<TextField> validationFields = new ArrayList<>();

    public static Config getConfig() {
        return config;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        radioButtonGroup1 = new ToggleGroup();
        black1.setToggleGroup(radioButtonGroup1);
        orange1.setToggleGroup(radioButtonGroup1);
        green1.setToggleGroup(radioButtonGroup1);
        yellow1.setToggleGroup(radioButtonGroup1);
        radioButtonGroup2 = new ToggleGroup();
        black2.setToggleGroup(radioButtonGroup2);
        orange2.setToggleGroup(radioButtonGroup2);
        green2.setToggleGroup(radioButtonGroup2);
        yellow2.setToggleGroup(radioButtonGroup2);
        validationFields.add(txtP1);
        validationFields.add(txtP2);


        //TODO remove mock data
        txtP1.setText("Mirko");
        txtP2.setText("Slavko");
        orange1.setSelected(true);
        yellow2.setSelected(true);
    }

    public void selectTrack() throws IOException {
        if (notValid()){
            lblError.setText("Fill all fields");
            return;
        }

        String color1 = ((RadioButton) radioButtonGroup1.getSelectedToggle()).getId();
        String color2 = ((RadioButton) radioButtonGroup2.getSelectedToggle()).getId();
        color1 = color1.replace(color1.substring(color1.length() - 1), "").toUpperCase();
        color2 = color2.replace(color2.substring(color2.length() - 1), "").toUpperCase();

        config = new Config(
                        new PlayerDetails(txtP1.getText(), CarColor.valueOf(color1)),
                        new PlayerDetails(txtP2.getText(), CarColor.valueOf(color2)));

        FxmlUtils.showScreen("trackSelection.fxml", StartApplication.getMainStage());
    }

    private boolean notValid() {
        for (TextField field : validationFields) {
            if (field.getText().trim().isBlank()) { return true; }
        }
        if (radioButtonGroup1.getSelectedToggle() == null || radioButtonGroup2.getSelectedToggle() == null
                || !radioButtonGroup1.getSelectedToggle().isSelected() || !radioButtonGroup2.getSelectedToggle().isSelected()) {
            return true;
        }

        return false;
    }
}
