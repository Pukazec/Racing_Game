package leo.skvorc.racinggame.results;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ResultsApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(ResultsApplication.class.getResource("results.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Results!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
