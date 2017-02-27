package exception;

public class UserException extends Exception {
  public UserException() {
  }

  public UserException(String message) {
    super(message);
  }

  public UserException(Throwable throwable) {
    super(throwable);
  }

  public UserException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
