package exception.game;

public class GameException extends Exception {
  public GameException() {
  }

  public GameException(String message) {
    super(message);
  }

  public GameException(Throwable throwable) {
    super(throwable);
  }

  public GameException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
