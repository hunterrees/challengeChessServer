package dao;

import exception.game.GameNotFoundException;
import model.Move;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class MoveDaoTest {

  private MoveDao testModel;

  private Move move0;
  private Move move1;
  private Move move2;

  @BeforeMethod
  public void setUp() {
    testModel = new MoveDao();
    move0 = new Move("start", "end", "result");
    move1 = new Move("start1", "end1", "result1");
    move2 = new Move("start2", "end2", "result2");
  }

  @Test
  public void shouldCreateGameWithInitialMove() throws GameNotFoundException {
    testModel.addMove(0, move0);
    move0.setGameId(0);
    move0.setId(0);

    List<Move> result = testModel.getMovesForGame(0);

    assertEquals(result.get(0), move0);
  }

  @Test
  public void shouldAddMove() throws GameNotFoundException {
    move0.setGameId(0);
    move0.setId(0);

    testModel.addMove(0, move1);
    move1.setGameId(0);
    move1.setId(1);

    List<Move> result = testModel.getMovesForGame(0);

    assertEquals(result.size(), 2);
    assertEquals(result.get(1), move1);
  }

  @Test
  public void shouldGetMovesForGame() throws GameNotFoundException {
    testModel.addMove(0, move0);
    List<Move> result = testModel.getMovesForGame(0);

    assertEquals(result.size(), 1);
  }

  @Test (expectedExceptions = GameNotFoundException.class, expectedExceptionsMessageRegExp = ".*Game 0.not found.*")
  public void shouldThrowExceptionWhenGameDoesNotExist() throws GameNotFoundException {
    testModel.getMovesForGame(0);
  }

  @Test (expectedExceptions = GameNotFoundException.class, expectedExceptionsMessageRegExp = ".*Game 0.not found.*")
  public void shouldThrowExceptionWhenAddingToGameThatDoesNotExist() throws GameNotFoundException {
    testModel.addMove(0, move2);
  }
}
