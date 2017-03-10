package facade;

import dao.GameDAO;
import dao.UserDAO;
import exception.game.GameException;
import exception.game.GameNotFoundException;
import exception.user.InvalidUserCookieException;
import model.Game;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class GameFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameFacade.class);

  private GameDAO gameDAO;
  private UserDAO userDAO;

  /**
   * Default Constructor.
   */
  public GameFacade() {
    gameDAO = new GameDAO();
    userDAO = new UserDAO();
  }

  /**
   * Constructor for unit testing.
   *
   * @param gameDAO GameDAO to access game data.
   * @param userDAO UserDAO to access user data.
   */
  public GameFacade(GameDAO gameDAO, UserDAO userDAO) {
    this.gameDAO = gameDAO;
    this.userDAO = userDAO;
  }

  /**
   * Returns all games a user has played in.
   *
   * @param username non-null string which is the player's username
   * @return List of games the user has played in.
   */
  public List<Game> getUserGames(String username) {
    return gameDAO.getUserGames(username);
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
    validateCookie(username, userCookie);
    return gameDAO.getGame(gameId);
  }

  /**
   * Creates a game with the two specified players.
   *
   * @param player1 non-null string of first player to play in game.
   * @param player2 non-null string of second player to play in game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @return Created game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws GameException if game has the same player twice.
   */
  public Game createGame(String player1, String player2, String userCookie)
          throws InvalidUserCookieException, GameException {
    validateCookie(player1, userCookie);
    return gameDAO.createGame(player1, player2);
  }

  /**
   * Creates a game with another random user.
   *
   * @param username Player requesting to create a new game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @return Created game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   */
  public Game createRandomGame(String username, String userCookie) throws InvalidUserCookieException, GameException {
    List<User> users = userDAO.getAllUsers();
    String player2 = username;
    while (player2.equals(username) || player2.equals("pamela")) {
      player2 = users.get(new Random().nextInt(users.size())).getUsername();
    }
    return createGame(username, player2, userCookie);
  }

  /**
   * Creates a game with the machine learning AI.
   *
   * @param username Player requesting to create a new game.
   * @param userCookie Cookie of the user attempting to create the game.
   * @return Created game.
   * @throws InvalidUserCookieException if user cookie is invalid.
   */
  public Game createMachineLearningGame(String username, String userCookie)
          throws InvalidUserCookieException, GameException {
    return createGame(username, "pamela", userCookie);
  }

  /**
   * Checks the user cookie.
   *
   * @param username non-null string of the user.
   * @param userCookie non-null string of the user's cookie.
   * @throws InvalidUserCookieException if user cookie is invalid.
   */
  private void validateCookie(String username, String userCookie) throws InvalidUserCookieException {

  }
}
