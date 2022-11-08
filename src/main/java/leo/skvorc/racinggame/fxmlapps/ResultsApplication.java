package leo.skvorc.racinggame.fxmlapps;

import javafx.application.Application;
import javafx.stage.Stage;
import leo.skvorc.racinggame.utils.FxmlUtils;

public class ResultsApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FxmlUtils.showScreen("results.fxml", stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
