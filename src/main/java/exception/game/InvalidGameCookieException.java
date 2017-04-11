package exception.game;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value= HttpStatus.FORBIDDEN, reason="Invalid game cookie")
public class InvalidGameCookieException extends GameException {
  public InvalidGameCookieException(String message) {
    super(message);
  }
}
