package ch.bbw.chattrix.config;

public class UsernameOrPasswordWrongException extends RuntimeException {
    public UsernameOrPasswordWrongException() {
        super("Username or Pasword is wrong");
    }
}