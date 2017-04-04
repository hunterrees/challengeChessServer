package api;

import exception.ServerException;
import exception.game.GameException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import facade.GameFacade;
import manager.CookieManager;
import model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static api.EntryPoint.USER_COOKIE;

@RestController
@EnableAutoConfiguration
@RequestMapping("games")
public class GameEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameEndpoint.class);

  private GameFacade gameFacade;
  private CookieManager cookieManager;

  /**
   * Default constructor.
   */
  public GameEndpoint() {
    gameFacade = new GameFacade();
    cookieManager = CookieManager.getInstance();
  }

  /**
   * Constructor for unit-testing.
   * @param gameFacade Game Facade to hit.
   * @param cookieManager Cookie Manager to use.
   */
  GameEndpoint(GameFacade gameFacade, CookieManager cookieManager) {
    this.gameFacade = gameFacade;
    this.cookieManager = cookieManager;
  }

  /**
   * Returns all games a user has played in.
   *
   * @param username non-null string of the user to get games for.
   * @param cookie userCookie of the user requesting the games.
   * @return list of all games the user had been in.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws UserNotFoundException when user does not exist.
   */
  @RequestMapping(value="{username}", method=RequestMethod.GET)
  List<Game> getUserGames(@PathVariable String username, @RequestParam(USER_COOKIE) String cookie)
          throws InvalidUserCookieException, UserNotFoundException {
    List<Game> userGames;
    try {
      LOGGER.info("/games/{username} GET hit by {} with cookie {}", username, cookie);
      userGames = gameFacade.getUserGames(username, cookie);
    }
    catch (RuntimeException e) {
      LOGGER.error("Error in /games/{username} GET {}", e);
      throw new ServerException(e);
    }
    return userGames;
  }

  /**
   * Creates a new game between the user requesting the game and a random player.
   *
   * @param cookie userCookie of the user requesting to create a game.
   * @throws GameException if the game has the same user twice.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws UserNotFoundException when user does not exist.
   */
  @RequestMapping(value="create", method=RequestMethod.POST)
  void createRandomGame(@RequestParam(USER_COOKIE) String cookie)
          throws GameException, UserNotFoundException, InvalidUserCookieException {
    try {
      LOGGER.info("/games/create POST hit with cookie {}", cookie);
      String username = cookieManager.getUsername(cookie);
      gameFacade.createRandomGame(username, cookie);
    }
    catch (RuntimeException e) {
      LOGGER.error("Error in /games/create POST GET {}", e);
      throw new ServerException(e);
    }
  }

  /**
   * Creates a new game between the user requesting the game and the other player.
   *
   * @param username non-null string of the user to create a game with.
   * @param cookie userCookie of the user requesting to create a game.
   * @throws GameException if the game has the same user twice.
   * @throws InvalidUserCookieException if user cookie is invalid.
   * @throws UserNotFoundException when user does not exist.
   */
  @RequestMapping(value="create/{username}", method=RequestMethod.POST)
  void createGame(@PathVariable String username, @RequestParam(USER_COOKIE) String cookie)
          throws GameException, UserNotFoundException, InvalidUserCookieException {
    try {
      LOGGER.info("/games/create/{username} POST hit with cookie {}", cookie);
      String player1 = cookieManager.getUsername(cookie);
      gameFacade.createGame(player1, username, cookie);
    }
    catch (RuntimeException e) {
      LOGGER.error("Error in /games/create/{username} POST GET {}", e);
      throw new ServerException(e);
    }
  }
}
