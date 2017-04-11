package api;

import exception.game.GameException;
import exception.game.GameNotFoundException;
import exception.user.UserException;
import facade.MoveFacade;
import model.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static api.EntryPoint.GAME_COOKIE;
import static api.EntryPoint.USER_COOKIE;

@RestController
@EnableAutoConfiguration
@RequestMapping("moves")
class MoveEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(MoveEndpoint.class);

  private final MoveFacade moveFacade;

  /**
   * Default Constructor.
   */
  @SuppressWarnings ("unused")
  public MoveEndpoint() {
    moveFacade = new MoveFacade();
  }

  /**
   * Constructor for unit-testing.
   *
   * @param moveFacade Move Facade to hit.
   */
  MoveEndpoint(MoveFacade moveFacade) {
    this.moveFacade = moveFacade;
  }

  /**
   * Retrieves the list of all moves currently in the game.
   *
   * @param gameId positive integer id of the game to add the move to.
   * @param userCookie cookie of the user (used for verification).
   * @param gameCookie cookie of the game (used for verification).
   * @return List of moves for the game requested.
   * @throws GameNotFoundException if the game with the given id given does not exist.
   */
  @RequestMapping(value="{gameId}", method=RequestMethod.GET)
  List<Move> getMovesForGame(@PathVariable int gameId, @RequestParam(USER_COOKIE) String userCookie,
                                    @RequestParam(GAME_COOKIE) String gameCookie) throws GameException, UserException {
    LOGGER.info("Requesting moves for game {} with user cookie {} and game cookie {}", gameId, userCookie, gameCookie);
    return moveFacade.getMovesForGame(gameId, userCookie, gameCookie);
  }

  /**
   * Plays a move on the specified game. Must be verified by the other player to take effect.
   *
   * @param gameId positive integer id of the game to add the move to.
   * @param move non-null Move object to be added to the database.
   * @param userCookie cookie of the user (used for verification).
   * @param gameCookie cookie of the game (used for verification).
   * @throws GameNotFoundException if the game with the given id given does not exist.
   */
  @RequestMapping(value="{gameId}", method=RequestMethod.POST)
  void playMove(@PathVariable int gameId, @RequestBody Move move, @RequestParam(USER_COOKIE) String userCookie,
                       @RequestParam(GAME_COOKIE) String gameCookie) throws GameException, UserException {
    LOGGER.info("Playing move {} in game {} with user cookie {} and game cookie {}",
            move, gameId, userCookie, gameCookie);
    moveFacade.makeMove(move, userCookie, gameCookie);
  }

  /**
   * Verifies that the given move is valid in the game. Adds the move to the database.
   *
   * @param gameId positive integer id of the game.
   * @param move non-null Move object to be verified.
   * @param userCookie cookie of the user (used for verification).
   * @param gameCookie cookie of the game (used for verification).
   * @throws GameNotFoundException if the game with the given id given does not exist.
   */
  @RequestMapping(value="{gameId}/{moveId}", method=RequestMethod.PUT)
  void verifyMove(@PathVariable int gameId, @RequestBody Move move, @RequestParam(USER_COOKIE) String userCookie,
                         @RequestParam(GAME_COOKIE) String gameCookie) throws GameException, UserException {
    LOGGER.info("Verifying move {} in game {} with user cookie {} and game cookie {}",
            move, gameId, userCookie, gameCookie);
    moveFacade.verifyMove(move, userCookie, gameCookie);
  }
}
