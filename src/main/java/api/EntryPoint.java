package api;


import exception.ServerException;
import exception.user.UserNotFoundException;
import manager.EncryptionManager;
import manager.ClientConnectionManager;
import model.DHParams;
import model.ConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class EntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.class);

  private ClientConnectionManager clientConnectionManager;
  private EncryptionManager encryptionManager;

  public static final String MACHINE_AI_USERNAME = "pamela";
  final static String USER_COOKIE = "userCookie";
  final static String GAME_COOKIE = "gameCookie";

  /**
   * Default constructor.
   */
  public EntryPoint() {
    clientConnectionManager = ClientConnectionManager.getInstance();
    encryptionManager = EncryptionManager.getInstance();
  }

  /**
   * Constructor for unit testing.
   *
   * @param clientConnectionManager ClientConnectionManager to use.
   * @param encryptionManager EncryptionManager to use.
   */
  EntryPoint(ClientConnectionManager clientConnectionManager, EncryptionManager encryptionManager) {
    this.clientConnectionManager = clientConnectionManager;
    this.encryptionManager = encryptionManager;
  }

  /**
   * Sets up a connection with the given user.
   * This connection will be used to notify the user of created games or moves.
   *
   * @param username non-null string of the user who is asking for a connection.
   * @param connectionInfo Host and port of the client.
   */
  @RequestMapping (value="connection/{username}", method=RequestMethod.POST)
  void setUpConnection(@PathVariable String username, @RequestBody ConnectionInfo connectionInfo) {
    try {
      LOGGER.info("/connection/{username} POST hit by {} with {}", username, connectionInfo);
      clientConnectionManager.setUpConnection(username, connectionInfo);
    } catch(RuntimeException e) {
      LOGGER.error("Error in /connection/{username} POST {}", e);
      throw new ServerException(e);
    }
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
      return encryptionManager.getPublicParameters(username);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /crypt/init/{username} GET {}", e);
      throw new ServerException(e);
    }
  }

  /**
   * Creates shared key based on the public parameter given in the body.
   *
   * @param username non-null string of the user who is requesting Diffie-Hellman.
   * @param parameters Diffie-Hellman parameters of the user.
   * @throws UserNotFoundException if user hasn't requested the initial public parameters.
   */
  @RequestMapping(value="crypt/end/{username}", method=RequestMethod.POST)
  void setUpSharedKey(@PathVariable String username, @RequestBody DHParams parameters) throws UserNotFoundException {
    try {
      LOGGER.info("/crypt/end/{username} POST hit with username {} and parameters {}", username, parameters);
      encryptionManager.generateSharedKey(username, parameters);
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
