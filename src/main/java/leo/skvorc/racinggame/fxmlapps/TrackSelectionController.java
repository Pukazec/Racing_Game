package leo.skvorc.racinggame.fxmlapps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.model.PlayerMetaData;
import leo.skvorc.racinggame.server.Server;
import leo.skvorc.racinggame.utils.ImageLoader;
import leo.skvorc.racinggame.utils.SerializerDeserializer;
import leo.skvorc.racinggame.utils.SocketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TrackSelectionController extends SocketListener implements Initializable {

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

    private static Map<Long, PlayerMetaData> playersMetadata = new HashMap<>();

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

        serializeConfig();
        sendDataToServer();
        lblError.setText("Waiting for players");
        this.acceptRequests(playersMetadata.get(ProcessHandle.current().pid()).getPort());
    }

    private void serializeConfig() {
        if (rbTrack1.isSelected()) {
            config.setTrack(1); }
        else {
            config.setTrack(2);
        }
        config.setNumLaps((int) sliderNumOfLaps.getValue());
        SerializerDeserializer.saveConfig(config);
    }


    private void sendDataToServer() {
        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)){
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            System.out.println("Connecting to address: " + clientSocket.getLocalAddress().toString().substring(1));

            PlayerMetaData newPlayerMetaData = new PlayerMetaData(clientSocket.getLocalAddress().toString().substring(1),
                    clientSocket.getPort(), config,ProcessHandle.current().pid());
            playersMetadata.put(ProcessHandle.current().pid(), newPlayerMetaData);

            oos.writeObject(newPlayerMetaData);
            System.out.println("Object metadata sent to server!");

            Integer readObject = (Integer) ois.readObject();
            playersMetadata.get(ProcessHandle.current().pid()).setPort(readObject);
            System.err.println("Player port: " + playersMetadata.get(ProcessHandle.current().pid()).getPort());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())){
            String answer = (String) ois.readObject();
            System.err.println(answer);

            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
