package exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value= HttpStatus.NOT_FOUND, reason="No such User")
public class UserNotFoundException extends UserException {
  public UserNotFoundException() {
  }

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(Throwable throwable) {
    super(throwable);
  }

  public UserNotFoundException(String message, Throwable throwable) {
    super(message, throwable);
  }
}