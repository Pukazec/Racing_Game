package leo.skvorc.racinggame.fxmlapps;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.utils.ImageLoader;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackSelectionController implements Initializable {

    @FXML
    private RadioButton rbTrack1;

    @FXML
    private RadioButton rbTrack2;

    @FXML
    private ImageView imgTrack1;

    @FXML
    private ImageView imgTrack2;

    @FXML
    private Slider sliderNumOfLaps;

    @FXML
    private Label lblError;

    ToggleGroup radioButtonGroup;

    private static Config config;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        config = StartController.getConfig();
        radioButtonGroup = new ToggleGroup();
        rbTrack1.setToggleGroup(radioButtonGroup);
        rbTrack2.setToggleGroup(radioButtonGroup);

        imgTrack1.setImage(ImageLoader.loadImage(Config.TRACK1));
        imgTrack2.setImage(ImageLoader.loadImage(Config.TRACK2));

        rbTrack2.setSelected(true);
    }

    public void startRacing(){
        if (radioButtonGroup.getSelectedToggle() == null
                || !radioButtonGroup.getSelectedToggle().isSelected()) {
            lblError.setText("Fill all fields");
            return;
        }

        if (rbTrack1.isSelected()) {
        config.setTrack(1); }
        else {
            config.setTrack(2);
        }
        config.setNumLaps((int) sliderNumOfLaps.getValue());

        SerializerDeserializer.saveConfig(config);

        Platform.exit();
    }
}
