package facade;

import dao.GameDao;
import dao.MoveDao;
import exception.game.GameException;
import exception.game.GameNotFoundException;
import exception.game.InvalidGameCookieException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import manager.ClientConnectionManager;
import manager.CookieManager;
import model.Game;
import model.GameStatus;
import model.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(MoveFacade.class);

  private final ClientConnectionManager clientConnectionManager;
  private final CookieManager cookieManager;
  private final MoveDao moveDao;
  private final GameDao gameDao;
  private final Map<String, Move> movesToVerify;

  /**
   * Default constructor.
   */
  public MoveFacade() {
    clientConnectionManager = ClientConnectionManager.getInstance();
    moveDao = MoveDao.getInstance();
    cookieManager = CookieManager.getInstance();
    gameDao = GameDao.getInstance();
    movesToVerify = new HashMap<>();
  }

  /**
   * Constructor for unit tests.
   */
  MoveFacade(ClientConnectionManager clientConnectionManager, CookieManager cookieManager,
             MoveDao moveDao, GameDao gameDao) {
    this.clientConnectionManager = clientConnectionManager;
    this.moveDao = moveDao;
    this.cookieManager = cookieManager;
    this.gameDao = gameDao;
    movesToVerify = new HashMap<>();
  }

  /**
   * Gets all moves for a specified game.
   *
   * @param gameId     non-negative integer corresponding to the game.
   * @param userCookie non-null String
   * @param gameCookie non-null String
   * @return list of all moves for the requested game.
   * @throws GameNotFoundException      when the game does not exist.
   * @throws UserNotFoundException      if cookie is for a different user.
   * @throws InvalidUserCookieException if not a valid user cookie
   * @throws InvalidGameCookieException if not a valid game cookie
   */
  public List<Move> getMovesForGame(int gameId, String userCookie, String gameCookie)
          throws InvalidGameCookieException, GameNotFoundException, InvalidUserCookieException, UserNotFoundException {
    LOGGER.info("MoveFacade.getMovesForGame");
    cookieManager.validateCookies(userCookie, gameCookie);
    if (Integer.parseInt(cookieManager.getQualifier(gameCookie)) != gameId) {
      throw new InvalidGameCookieException("Game cookie doesn't match game id you are requesting");
    }
    return moveDao.getMovesForGame(gameId);
  }

  //make move

  /**
   * Sends the move to the other player to be verified.
   *
   * @param move       non null Move attempting to be made.
   * @param userCookie non-null String
   * @param gameCookie non-null String
   * @throws GameNotFoundException      when the game does not exist.
   * @throws UserNotFoundException      if cookie is for a different user.
   * @throws InvalidUserCookieException if not a valid user cookie
   * @throws InvalidGameCookieException if not a valid game cookie
   */
  public void makeMove(Move move, String userCookie, String gameCookie)
          throws InvalidGameCookieException, GameNotFoundException, InvalidUserCookieException, UserNotFoundException {
    LOGGER.info("MoveFacade.makeMove");
    cookieManager.validateCookies(userCookie, gameCookie);
    String requestingUser = cookieManager.getQualifier(userCookie);
    Game game = gameDao.getGame(Integer.parseInt(cookieManager.getQualifier(gameCookie)));
    String otherPlayer;
    LOGGER.info("Move will be added to game {}", game);
    if (game.getPlayer1().equals(requestingUser)) {
      otherPlayer = game.getPlayer2();
    }
    else {
      otherPlayer = game.getPlayer1();
    }
    LOGGER.info("Adding {} to moves to be verified", move);
    movesToVerify.put(otherPlayer, move);
    clientConnectionManager.sendData(otherPlayer, move);
  }

  //verify move

  /**
   * Makes sure the move was previously made and adds it to the game.
   *
   * @param move       non null Move attempting to be made.
   * @param userCookie non-null String
   * @param gameCookie non-null String
   * @throws GameNotFoundException      when the game does not exist.
   * @throws UserNotFoundException      if cookie is for a different user.
   * @throws InvalidUserCookieException if not a valid user cookie
   * @throws InvalidGameCookieException if not a valid game cookie
   * @throws GameException              if that move is not meant to be validated
   */
  public void verifyMove(Move move, String userCookie, String gameCookie)
          throws InvalidUserCookieException, UserNotFoundException, GameException {
    LOGGER.info("MoveFacade.verifyMove");
    cookieManager.validateCookies(userCookie, gameCookie);
    String validatingUser = cookieManager.getQualifier(userCookie);
    if (movesToVerify.containsKey(validatingUser) && movesToVerify.get(validatingUser).equals(move)) {
      LOGGER.info("Move was verified and is being added to the game.");
      movesToVerify.remove(validatingUser);
      int gameId = Integer.parseInt(cookieManager.getQualifier(gameCookie));
      moveDao.addMove(gameId, move);
      if (!move.getResult().equals("")) {
        gameDao.updateGame(gameId, GameStatus.convert(move.getResult()));
      }
    }
    else {
      throw new GameException("Move not meant to validated.");
    }
  }
}
