package org.alexdev.kepler.console;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.commands.CommandManager;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ConsoleReader implements Runnable {

    public ConsoleOperator operator;
    public static ConsoleReader instance;

    public ConsoleReader() {
        this.operator = new ConsoleOperator();
        this.createTask();
    }

    /**
     * Create the task for reading console input
     */
    private void createTask() {
        GameScheduler.getInstance().getSchedulerService().schedule(this, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = ":" + scanner.nextLine();

            if (CommandManager.getInstance().hasCommand(this.operator, command)) {
                CommandManager.getInstance().invokeCommand(this.operator, command);
            } else {
                System.out.println("That command does not exist.");
            }

            scanner.close();
        }
    }

    /**
     * Get the instance for this class
     *
     * @return the instance
     */
    public static ConsoleReader getInstance() {

        if (instance == null) {
            instance = new ConsoleReader();
        }

        return instance;
    }
}
