package leo.skvorc.racinggame.utils;

import leo.skvorc.racinggame.RacingApp;
import leo.skvorc.racinggame.fxmlapps.StartApplication;

public class ApplicationStarter {
    public static void main(String[] args) {
        Thread start = new Thread();

        StartApplication startApplication = new StartApplication();
        start.start(startApplication.run());
    }
}
