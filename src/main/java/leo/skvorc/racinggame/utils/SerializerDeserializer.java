package leo.skvorc.racinggame.utils;

import leo.skvorc.racinggame.Config;

import java.io.*;

public class SerializerDeserializer {

    public static void saveConfig(Config config) {
        try (ObjectOutputStream serializer = new ObjectOutputStream(new FileOutputStream("saveConfig.ser"))) {
            serializer.writeObject(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Config loadConfig() {
        try (ObjectInputStream deserializer = new ObjectInputStream(new FileInputStream("saveConfig.ser"))) {
            return  (Config) deserializer.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
