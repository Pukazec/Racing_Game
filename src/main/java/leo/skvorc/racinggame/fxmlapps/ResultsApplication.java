package leo.skvorc.racinggame.fxmlapps;

import javafx.application.Application;
import javafx.stage.Stage;
import leo.skvorc.racinggame.utils.FxmlUtils;

public class ResultsApplication extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {

        FxmlUtils.showScreen("resultsSelectPlayer.fxml", stage);
        mainStage = stage;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
