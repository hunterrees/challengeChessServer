package dao;

import exception.game.GameException;
import exception.game.GameNotFoundException;
import model.Game;
import model.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameDAO.class);

  private List<Game> games;

  public GameDAO() {
    games = new ArrayList<>();
  }

  /**
   * Gets all games that a player has played in (regardless of status).
   *
   * @param username non-null string of the user who you want games for.
   * @return List of games the user has played in.
   */
  public List<Game> getUserGames(String username) {
    LOGGER.info("Getting all games for {}", username);
    List<Game> result = new ArrayList<>();
    for (Game game : games) {
      if (game.getPlayer1().equals(username) || game.getPlayer2().equals(username)) {
        result.add(game);
      }
    }
    return result;
  }

  /**
   * Creates a new game with the two players and sets the status to playing.
   *
   * @param player1 non-null string of first player to play in game.
   * @param player2 non-null string of second player to play in game.
   * @throws GameException if game has the same player twice.
   * @return Created game.
   */
  public Game createGame(String player1, String player2) throws GameException {
    LOGGER.info("Creating a game with users {} and {}", player1, player2);
    if (player1.equals(player2)) {
      throw new GameException("Can't have a game with the same player twice.");
    }
    Game game = new Game(games.size(), player1, player2, GameStatus.PLAYING);
    games.add(game);
    return game;
  }

  /**
   * Updates the game status of the given game-id.
   *
   * @param gameId Id of the game in the database.
   * @param status New status of the game.
   * @throws GameNotFoundException if game does not exist.
   */
  public void updateGame(int gameId, GameStatus status) throws GameNotFoundException {
    LOGGER.info("Updating game status of {} to {}", gameId, status);
    if (gameId >= games.size()) {
      throw new GameNotFoundException("Game does not exist");
    }
    games.get(gameId).setStatus(status);
  }

  /**
   * Gets a specific game.
   *
   * @param gameId Id of the game in the database.
   * @return Game that is specified by the given id.
   * @throws GameNotFoundException if game does not exist.
   */
  public Game getGame(int gameId) throws GameNotFoundException {
    LOGGER.info("Getting game {}", gameId);
    if (gameId >= games.size()) {
      throw new GameNotFoundException("Game does not exist");
    }
    return games.get(gameId);
  }
}
