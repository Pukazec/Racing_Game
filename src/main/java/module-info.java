open module racinggame.main {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires annotations;
    requires java.rmi;
    requires java.naming;

    exports leo.skvorc.racinggame;
    exports leo.skvorc.racinggame.fxmlapps;
}