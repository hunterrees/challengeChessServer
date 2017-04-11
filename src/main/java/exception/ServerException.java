package exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Internal Server Error")
public class ServerException extends RuntimeException{
  public ServerException() {
  }

  public ServerException(Throwable throwable) {
    super(throwable);
  }
}
