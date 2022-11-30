package leo.skvorc.racinggame.utils;

import javafx.application.Platform;
import leo.skvorc.racinggame.RacingApp;
import leo.skvorc.racinggame.fxmlapps.StartApplication;

public class ApplicationStarter {
    public static void main(String[] args) {

        Thread newThread = new Thread(new StartApplication());
        Thread secondThread = new Thread(new RacingApp());
        //System.out.println("Starting configuration");
        //StartApplication startApplication = ;
        //startApplication.run();
        newThread.run();
        System.out.println(newThread.isDaemon());
        newThread.interrupt();
        newThread.setDaemon(true);
        System.out.println(newThread.isDaemon());

        System.out.println("Configuration done");

        System.out.println("Starting racing");
        secondThread.run();
        //System.out.println("Racing done");
    }
}
