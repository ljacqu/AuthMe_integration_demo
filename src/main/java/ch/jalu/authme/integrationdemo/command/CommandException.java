package ch.jalu.authme.integrationdemo.command;

/**
 * Exception during the execution of a command.
 */
public class CommandException extends Exception {

    /**
     * Constructor.
     *
     * @param message the failure message to return to the player
     */
    public CommandException(String message) {
        super(message);
    }

}
