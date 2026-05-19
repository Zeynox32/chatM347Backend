package ch.bbw.chattrix.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("E-Mail existiert bereits");
    }
}