open module racinggame.main {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires annotations;

    //opens leo.skvorc.racinggame to javafx.fxml;
    exports leo.skvorc.racinggame;
    exports leo.skvorc.racinggame.fxmlapps;
    exports leo.skvorc.racinggame.model;
}