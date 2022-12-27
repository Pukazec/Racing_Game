package leo.skvorc.racinggame.fxmlapps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import leo.skvorc.racinggame.Config;
import leo.skvorc.racinggame.model.CarColor;
import leo.skvorc.racinggame.model.PlayerDetails;
import leo.skvorc.racinggame.model.PlayerMetaData;
import leo.skvorc.racinggame.network.Server;
import leo.skvorc.racinggame.utils.FxmlUtils;
import leo.skvorc.racinggame.utils.JndiUtils;
import leo.skvorc.racinggame.utils.SerializerDeserializer;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

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
    private Label lblError;
    private ToggleGroup radioButtonGroup1;

    private static String HOST;
    private static int PORT;

    private static Config config;

    private final List<TextField> validationFields = new ArrayList<>();

    private static Map<Long, PlayerMetaData> playersMetadata = new HashMap<>();

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

        try {
            String serverPortString = JndiUtils.getConfigurationParameter("server.port");
            PORT = Integer.parseInt(serverPortString);
            HOST = JndiUtils.getConfigurationParameter("server.host");
            //TODO remove mock data
            txtP1.setText(JndiUtils.getConfigurationParameter("player.name"));
            green1.setSelected(true);
        } catch (NamingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectTrack() throws IOException {
        if (notValid()){
            lblError.setText("Fill all fields");
            return;
        }

        String color1 = ((RadioButton) radioButtonGroup1.getSelectedToggle()).getId();
        color1 = color1.replace(color1.substring(color1.length() - 1), "").toUpperCase();

        config = new Config(new PlayerDetails(txtP1.getText(), CarColor.valueOf(color1)));

        FxmlUtils.showScreen("trackSelection.fxml", StartApplication.getMainStage());
    }

    public void useOldConfig() {
        config = SerializerDeserializer.loadConfig();
        sendDataToServer();
        lblError.setText("Waiting for players");
        acceptRequests(playersMetadata.get(ProcessHandle.current().pid()).getPort());
    }

    private void sendDataToServer() {
        try (Socket clientSocket = new Socket(HOST, PORT)) {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            System.out.println("Connecting to address: " + clientSocket.getLocalAddress().toString().substring(1));

            PlayerMetaData newPlayerMetaData = new PlayerMetaData(clientSocket.getLocalAddress().toString().substring(1),
                    clientSocket.getPort(), config, ProcessHandle.current().pid());
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

    private void acceptRequests(Integer port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.err.println("Server listening on port:" + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                new Thread(() -> processSerializableClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
            String answer = (String) ois.readObject();
            System.err.println(answer);

            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean notValid() {
        for (TextField field : validationFields) {
            if (field.getText().trim().isBlank()) { return true; }
        }
        if (radioButtonGroup1.getSelectedToggle() == null || !radioButtonGroup1.getSelectedToggle().isSelected() ) {
            return true;
        }

        return false;
    }
}
