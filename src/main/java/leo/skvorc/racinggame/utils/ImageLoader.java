package leo.skvorc.racinggame.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageLoader {

    public static Image loadImage(String path){
        File image = new File(path);
        String str = image.getAbsolutePath();
        FileInputStream input;
        try {
            input = new FileInputStream(str);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new Image(input);
    }
}
