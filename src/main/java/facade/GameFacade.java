package facade;

import dao.GameDao;
import exception.game.GameException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import manager.ClientConnectionManager;
import manager.CookieManager;
import model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameFacade.class);

  private GameDao gameDao;
  private Queue<String> userQueue;
  private CookieManager cookieManager;
  private ClientConnectionManager clientConnectionManager;

  /**
   * Default Constructor.
   */
  public GameFacade() {
    gameDao = GameDao.getInstance();
    userQueue = new LinkedList<>();
    cookieManager = CookieManager.getInstance();
    clientConnectionManager = ClientConnectionManager.getInstance();
  }

  /**
   * Constructor for unit testing.
   *
   * @param gameDao GameDao to access game data.
   */
  GameFacade(GameDao gameDao, CookieManager cookieManager,
                    ClientConnectionManager clientConnectionManager) {
    this.gameDao = gameDao;
    userQueue = new LinkedList<>();
    this.cookieManager = cookieManager;
    this.clientConnectionManager = clientConnectionManager;
  }

  /**
   * Returns all games a user has played in.
   *
   * @param username non-null string which is the player's username
   * @param userCookie Cookie of the user requesting the games.
   * @return List of games the user has played in.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws UserNotFoundException when user does not exist.
   */
  public List<Game> getUserGames(String username, String userCookie)
          throws InvalidUserCookieException, UserNotFoundException {
    LOGGER.info("Getting all games for user {}", username);
    cookieManager.validateUserCookie(userCookie);
    return gameDao.getUserGames(username);
  }

  /**
   * Creates a game with the two specified players.
   * Game object (and game cookie) is sent to both players via socket.
   *
   * @param player1 non-null string of first player to play in game.
   * @param player2 non-null string of second player to play in game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws GameException if the game has the same user twice.
   * @throws UserNotFoundException when user does not exist.
   */
  public void createGame(String player1, String player2, String userCookie)
          throws InvalidUserCookieException, GameException, UserNotFoundException {
    LOGGER.info("Creating game between {} and {}", player1, player2);
    cookieManager.validateUserCookie(userCookie);

    Game game = gameDao.createGame(player1, player2);
    clientConnectionManager.sendData(player1, game);
    clientConnectionManager.sendData(player2, game);

    String gameCookie = cookieManager.makeGameCookie(game.getGameId());
    clientConnectionManager.sendData(player1, gameCookie);
    clientConnectionManager.sendData(player2, gameCookie);
  }

  /**
   * Creates a game with another user.
   * If no other user is currently waiting for a game, user is added to the queue.
   *
   * @param username Player requesting to create a new game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws GameException if the game has the same user twice.
   * @throws UserNotFoundException when user does not exist.
   */
  public void createRandomGame(String username, String userCookie)
          throws InvalidUserCookieException, GameException, UserNotFoundException {
    LOGGER.info("Creating random game for {}", username);
    cookieManager.validateUserCookie(userCookie);
    if (!userQueue.isEmpty()) {
      String player2 = userQueue.poll();
      createGame(username, player2, userCookie);
    }
    else {
      userQueue.add(username);
    }
  }
}
