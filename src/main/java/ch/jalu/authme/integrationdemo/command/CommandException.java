package ch.jalu.authme.integrationdemo.command;

/**
 * Exception during the execution of a command.
 */
public class CommandException extends Exception {

    public CommandException(String message) {
        super(message);
    }

}
