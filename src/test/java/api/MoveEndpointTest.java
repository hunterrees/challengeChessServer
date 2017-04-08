package api;

import exception.ServerException;
import exception.game.GameException;
import exception.game.InvalidGameCookieException;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import facade.MoveFacade;
import model.Move;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class MoveEndpointTest {

  @Mock
  private MoveFacade moveFacade;

  private MoveEndpoint testModel;

  @BeforeMethod
  public void setUp() throws UserException, GameException {
    MockitoAnnotations.initMocks(this);

    List<Move> moves = new ArrayList<>();
    moves.add(new Move());
    moves.add(new Move());
    moves.add(new Move());

    when(moveFacade.getMovesForGame(eq(0), anyString(), anyString())).thenReturn(moves);
    testModel = new MoveEndpoint(moveFacade);
  }

  @Test
  public void shouldRetrieveMovesForGame() throws GameException, UserException {
    List<Move> result = testModel.getMovesForGame(0, "userCookie", "gameCookie");

    assertEquals(result.size(), 3);
  }

  @Test
  public void shouldPlayMove() throws GameException, UserException {
    testModel.playMove(0, new Move(), "userCookie", "gameCookie");
    verify(moveFacade).makeMove(any(), eq("userCookie"), eq("gameCookie"));
  }

  @Test
  public void shouldVerifyMove() throws GameException, UserException {
    testModel.verifyMove(0, new Move(), "userCookie", "gameCookie");
    verify(moveFacade).verifyMove(any(), eq("userCookie"), eq("gameCookie"));
  }

  @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
  public void shouldThrowExceptionForInvalidUserCookie() throws GameException, UserException {
    doThrow(new InvalidUserCookieException("Invalid User Cookie"))
            .when(moveFacade).makeMove(any(), eq("invalidUserCookie"), anyString());
    testModel.playMove(0, new Move(), "invalidUserCookie", "gameCookie");
  }

  @Test (expectedExceptions = InvalidGameCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid Game Cookie.*")
  public void shouldThrowExceptionForInvalidGameCookie() throws GameException, UserException {
    doThrow(new InvalidGameCookieException("Invalid Game Cookie"))
            .when(moveFacade).makeMove(any(), anyString(), eq("invalidGameCookie"));
    testModel.playMove(0, new Move(), "userCookie", "invalidGameCookie");
  }

  @Test (expectedExceptions = ServerException.class)
  public void shouldWrapIntoServerException() throws GameException, UserException {
    doThrow(new ServerException()).when(moveFacade).verifyMove(any(), anyString(), anyString());
    testModel.verifyMove(0, new Move(), "userCookie", "gameCookie");
  }
}
