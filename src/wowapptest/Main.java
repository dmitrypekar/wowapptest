package wowapptest;

import org.apache.log4j.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        initLogging();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.load();
        dispatcher.start();
        dispatcher.run();
    }

    private static void initLogging() {
        BasicConfigurator.resetConfiguration();

        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);

        root.addAppender(new ConsoleAppender(new PatternLayout("%d [%t] %-5p %c %x - %m%n")));
    }
}
