package me.zort.validationlib.exception;

/**
 * Exception thrown when a configuration is invalid or cannot be processed.
 *
 * @author ZorTik
 */
public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
