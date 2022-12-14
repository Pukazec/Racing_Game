package leo.skvorc.racinggame.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import leo.skvorc.racinggame.fxmlapps.StartApplication;

import java.io.IOException;

public class FxmlUtils {
    public static void showScreen(String fxmlFileName, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource(fxmlFileName));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Let's go racing!");
        stage.setScene(scene);
        stage.show();
    }
}
