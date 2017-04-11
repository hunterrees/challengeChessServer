package manager;

import dao.GameDao;
import dao.UserDao;
import exception.game.GameNotFoundException;
import exception.game.InvalidGameCookieException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import model.Game;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CookieManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(CookieManager.class);

  private final UserDao userDao;
  private final GameDao gameDao;

  private static CookieManager instance;

  private CookieManager() {
    userDao = UserDao.getInstance();
    gameDao = GameDao.getInstance();
  }

  CookieManager(UserDao userDao, GameDao gameDao) {
    this.userDao = userDao;
    this.gameDao = gameDao;
  }

  public static CookieManager getInstance() {
    if (instance == null) {
      instance = new CookieManager();
    }
    return instance;
  }

  public String makeUserCookie(String username) throws UserNotFoundException {
    LOGGER.info("Creating cookie for user {}", username);
    User user = userDao.getUser(username);
    String password = user.getPassword();
    String email = user.getEmail();
    String toHash = username + password + email;
    String hash = hash(toHash);
    LOGGER.info("Cookie created: {}", hash);
    return username + ":" + hash;
  }

  private String hash(String toHash) {
    MessageDigest sha256;
    try {
      sha256 = MessageDigest.getInstance("SHA-256");
      sha256.reset();
      sha256.update(toHash.getBytes());
      byte[] fullHash = sha256.digest();
      return new String(fullHash);
    }
    catch (NoSuchAlgorithmException e) {
      LOGGER.error("no such algorithm {}", e);
      return null;
    }
  }

  public void validateUserCookie(String cookie) throws UserNotFoundException, InvalidUserCookieException {
    String cookieUserName = getQualifier(cookie);
    String tempCookie = makeUserCookie(cookieUserName);
    if (!tempCookie.equals(cookie)) {
      throw new InvalidUserCookieException("Invalid User Cookie");
    }
  }

  public String makeGameCookie(int gameID) throws GameNotFoundException {
    LOGGER.info("Creating cookie for game {}", gameID);
    Game game = gameDao.getGame(gameID);
    String toHash = game.getPlayer1() + game.getPlayer2();
    String hash = hash(toHash);
    LOGGER.info("Cookie created: {}", hash);
    return gameID + ":" + hash;
  }

  void validateGameCookie(String cookie) throws GameNotFoundException, InvalidGameCookieException {
    int indexOfColon = cookie.indexOf(':');
    if (indexOfColon == -1) {
      throw new InvalidGameCookieException("Invalid Game Cookie");
    }
    int cookieGameId = Integer.parseInt(cookie.substring(0, indexOfColon));
    String tempCookie = makeGameCookie(cookieGameId);
    if (!tempCookie.equals(cookie)) {
      throw new InvalidGameCookieException("Invalid Game Cookie");
    }
  }

  public void validateCookies(String userCookie, String gameCookie)
          throws InvalidGameCookieException, GameNotFoundException, InvalidUserCookieException, UserNotFoundException {
    validateUserCookie(userCookie);
    validateGameCookie(gameCookie);
  }

  public String getQualifier(String cookie) throws InvalidUserCookieException {
    int indexOfColon = cookie.indexOf(':');
    if (indexOfColon == -1) {
      throw new InvalidUserCookieException("Invalid User Cookie");
    }
    return cookie.substring(0, indexOfColon);
  }
}
