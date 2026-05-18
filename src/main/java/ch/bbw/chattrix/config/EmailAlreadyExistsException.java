package ch.bbw.chattrix.config;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("E-Mail existiert bereits");
    }
}