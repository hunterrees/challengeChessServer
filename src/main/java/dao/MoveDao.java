package dao;

import exception.game.GameNotFoundException;
import model.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MoveDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(MoveDao.class);

  private final List<List<Move>> games;

  private static MoveDao instance;

  MoveDao() {
    games = new ArrayList<>();
  }

  public static MoveDao getInstance() {
    if (instance == null) {
      instance = new MoveDao();
    }
    return instance;
  }

  /**
   * Gets all moves for a specified game.
   *
   * @param gameId non-negative integer corresponding to the game.
   * @return list of all moves for the requested game.
   * @throws GameNotFoundException when the game does not exist.
   */
  public List<Move> getMovesForGame(int gameId) throws GameNotFoundException {
    LOGGER.info("Retrieving moves for game: {}", gameId);
    checkGameExists(gameId);
    return games.get(gameId);
  }

  /**
   * Adds a move to the specified game.
   *
   * @param gameId non-negative integer corresponding to the game.
   * @param move non-null Move object to be added.
   * @throws GameNotFoundException when the game does not exist.
   */
  public void addMove(int gameId, Move move) throws GameNotFoundException {
    LOGGER.info("Adding move {} to game {}", move, gameId);
    checkGameExists(gameId);
    if (gameId == games.size()) {
      games.add(new ArrayList<>());
    }
    games.get(gameId).add(move);
  }

  private void checkGameExists(int gameId) throws GameNotFoundException {
    if (gameId > games.size() || gameId < 0) {
      throw new GameNotFoundException(String.format("Game %s not found", gameId));
    }
  }
}
