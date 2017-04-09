package exception.game;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value= HttpStatus.NOT_FOUND, reason="No such Game")
public class GameNotFoundException extends GameException {
  public GameNotFoundException(String message) {
    super(message);
  }
}
