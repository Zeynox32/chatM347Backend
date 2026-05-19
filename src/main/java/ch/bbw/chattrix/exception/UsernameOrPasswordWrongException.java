package ch.bbw.chattrix.exception;

public class UsernameOrPasswordWrongException extends RuntimeException {
    public UsernameOrPasswordWrongException() {
        super("Username or Pasword is wrong");
    }
}