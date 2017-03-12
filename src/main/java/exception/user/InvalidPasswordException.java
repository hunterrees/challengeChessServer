package exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value= HttpStatus.BAD_REQUEST, reason="Invalid password")
public class InvalidPasswordException extends UserException {
  public InvalidPasswordException() {
  }

  public InvalidPasswordException(String message) {
    super(message);
  }

  public InvalidPasswordException(Throwable throwable) {
    super(throwable);
  }

  public InvalidPasswordException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
