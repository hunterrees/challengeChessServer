package dao;

import exception.game.GameException;
import exception.game.GameNotFoundException;
import model.Game;
import model.GameStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class GameDaoTest {

  private Game game;

  private GameDao testModel;

  @BeforeMethod
  public void setUp() {
    testModel = new GameDao();
    game = new Game(0, "player1", "player2", GameStatus.PLAYING);
  }

  @Test
  public void shouldAddGameToDatabase() throws GameException {
    testModel.createGame("player1", "player2");

    Game result = testModel.getGame(0);

    assertEquals(result, game);
  }

  @Test
  public void shouldGetGamesUserIsInvolvedIn() throws GameException {
    testModel.createGame("player1", "player2");
    testModel.createGame("player3", "player2");

    List<Game> result = testModel.getUserGames("player1");

    assertEquals(result.size(), 1);
    assertEquals(result.get(0), game);

    result = testModel.getUserGames("player2");

    assertEquals(result.size(), 2);
    assertEquals(result.get(0), game);
  }

  @Test
  public void shouldUpdateGameStatus() throws GameException {
    testModel.createGame("player1", "player2");
    testModel.createGame("player3", "player2");
    testModel.updateGame(1, GameStatus.PLAYER1_WIN);

    Game result = testModel.getGame(1);

    assertEquals(result.getStatus(), GameStatus.PLAYER1_WIN);
  }

  @Test (expectedExceptions = GameNotFoundException.class, expectedExceptionsMessageRegExp = ".*Game does not exist.*")
  public void shouldThrowExceptionIfChangingNonExistentGame() throws GameNotFoundException {
    testModel.updateGame(0, GameStatus.DRAW);
  }

  @Test (expectedExceptions = GameNotFoundException.class, expectedExceptionsMessageRegExp = ".*Game does not exist.*")
  public void shouldThrowExceptionIfGameDoesNotExist() throws GameNotFoundException {
    testModel.getGame(0);
  }

  @Test (expectedExceptions = GameException.class, expectedExceptionsMessageRegExp = ".*same player twice.*")
  public void shouldThrowExceptionIfPlayersAreIdentical() throws GameException {
    testModel.createGame("player1", "player1");
  }

  @Test
  public void shouldReturnEmptyListIfPlayerIsInNoGames() throws GameException {
    testModel.createGame("player1", "player2");
    testModel.createGame("player3", "player2");

    List<Game> result = testModel.getUserGames("player4");

    assertEquals(result.size(), 0);

  }
}
