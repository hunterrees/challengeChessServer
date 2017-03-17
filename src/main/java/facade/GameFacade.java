package facade;

import dao.GameDao;
import exception.game.GameException;
import exception.game.GameNotFoundException;
import exception.user.InvalidUserCookieException;
import model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameFacade.class);

  public static final String MACHINE_AI_USERNAME = "pamela";

  private GameDao gameDao;
  private Queue<String> userQueue;

  /**
   * Default Constructor.
   */
  public GameFacade() {
    gameDao = new GameDao();
    userQueue = new LinkedList<>();
  }

  /**
   * Constructor for unit testing.
   *
   * @param gameDao GameDao to access game data.
   */
  public GameFacade(GameDao gameDao) {
    this.gameDao = gameDao;
    userQueue = new LinkedList<>();
  }

  /**
   * Returns all games a user has played in.
   *
   * @param username non-null string which is the player's username
   * @param userCookie Cookie of the user requesting the games.
   * @return List of games the user has played in.
   */
  public List<Game> getUserGames(String username, String userCookie) {
    LOGGER.info("Getting all games for user {}", username);
    //TODO: Refactor so cookies are handled by CookieManager class
    return gameDao.getUserGames(username);
  }

  /**
   * Gets a specific game.
   *
   * @param gameId Id of the game in the database.
   * @param userCookie Cookie of the user requesting the game.
   * @return Game that is requested.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws GameNotFoundException if the game does not exist.
   */
  public Game getGame(int gameId, String username, String userCookie)
          throws InvalidUserCookieException, GameNotFoundException {
    LOGGER.info("Getting game {} for user {}", gameId, username);
    //TODO: Refactor so cookies are handled by CookieManager class
    return gameDao.getGame(gameId);
  }

  /**
   * Creates a game with the two specified players.
   * Game object (and game cookie) is sent to both players via socket.
   *
   * @param player1 non-null string of first player to play in game.
   * @param player2 non-null string of second player to play in game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws GameException if game has the same player twice.
   */
  public void createGame(String player1, String player2, String userCookie)
          throws InvalidUserCookieException, GameException {
    LOGGER.info("Creating game between {} and {}", player1, player2);
    //TODO: Refactor so cookies are handled by CookieManager class
    gameDao.createGame(player1, player2);
    //TODO: Send game and cookie on socket to both players (may have to refactor method signature)
  }

  /**
   * Creates a game with another user.
   * If no other user is currently waiting for a game, user is added to the queue.
   *
   * @param username Player requesting to create a new game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   */
  public void createRandomGame(String username, String userCookie) throws InvalidUserCookieException, GameException {
    LOGGER.info("Creating random game for {}", username);
    //TODO: Refactor so cookies are handled by CookieManager class
    if (!userQueue.isEmpty()) {
      String player2 = userQueue.poll();
      createGame(username, player2, userCookie);
    }
    else {
      userQueue.add(username);
    }
  }

  /**
   * Creates a game with the machine learning AI.
   *
   * @param username Player requesting to create a new game.
   * @param userCookie Cookie of the user attempting to create the game.
   *
   * @throws InvalidUserCookieException if user cookie is invalid.
   */
  public void createMachineLearningGame(String username, String userCookie)
          throws InvalidUserCookieException, GameException {
    LOGGER.info("Creating game for {} with pamela", username);
    //TODO: Refactor so cookies are handled by CookieManager class
    createGame(username, MACHINE_AI_USERNAME, userCookie);
  }
}
