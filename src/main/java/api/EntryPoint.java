package api;


import exception.ServerException;
import exception.user.UserNotFoundException;
import manager.EncryptionManager;
import model.DHParams;
import model.SocketInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.crypto.NoSuchPaddingException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class EntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.class);

  static Map<String, Socket> sockets;
  private EncryptionManager encryptionManager;

  public EntryPoint() throws NoSuchAlgorithmException, NoSuchPaddingException {
    sockets = new HashMap<>();
    encryptionManager = EncryptionManager.getInstance();
  }

  /**
   * Sets up a socket with the given user.
   * This socket will be used to notify the user of created games or moves.
   *
   * @param username non-null string of the user who is asking for a socket.
   * @param socketInfo Host and port of the client's socket.
   */
  @RequestMapping (value="socket/{username}", method=RequestMethod.POST)
  void setUpSocket(@PathVariable String username, @RequestBody SocketInfo socketInfo) {
    LOGGER.info("Socket endpoint hit by {} with {}", username, socketInfo);
  }

  /**
   * Sends initial Diffie-Hellman parameters to the user.
   * Also creates the public key that will be associated with the user.
   *
   * @param username non-null string of the user who is requesting the Diffie-Hellman key.
   * @return public Diffie-Hellman parameters.
   */
  @RequestMapping(value="crypt/init/{username}", method=RequestMethod.GET)
  DHParams sendParameters(@PathVariable String username) {
    try {
      LOGGER.info("/crypt/init/{username} GET hit with username {}", username);
      return encryptionManager.getParameters(username);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /crypt/init/{username} GET {}", e);
      throw new ServerException(e);
    }
  }

  /**
   * Creates shared key based on the public key given in the body.
   *
   * @param username non-null string of the user who is requesting Diffie-Hellman.
   * @param parameters Diffie-Hellman parameters of the user.
   * @throws UserNotFoundException if user hasn't requested the initial public parameters.
   */
  @RequestMapping(value="crypt/end/{username}", method=RequestMethod.POST)
  void setUpSharedKey(@PathVariable String username, @RequestBody DHParams parameters) throws UserNotFoundException {
    try {
      LOGGER.info("/crypt/end/{username} POST hit with username {} and parameters {}", username, parameters);
      encryptionManager.setUpSharedKey(username, parameters);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /crypt/end/{username} POST {}", e);
      throw new ServerException(e);
    }
  }

  public static void main(String[] args) throws Exception {
    Object[] sources = {EntryPoint.class, UserEndpoint.class, GameEndpoint.class, MoveEndpoint.class};
    LOGGER.info("Starting server with following classes: {}", sources);
    SpringApplication.run(sources, args);
  }
}
