package api;

import exception.game.GameException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import facade.GameFacade;
import model.Game;
import model.GameStatus;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class GameEndpointTest {

  @Mock
  private GameFacade gameFacade;

  private GameEndpoint testModel;

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    testModel = new GameEndpoint(gameFacade);
  }

  @Test
  public void shouldGetGamesForAUser() throws InvalidUserCookieException, UserNotFoundException {
    List<Game> expected = new ArrayList<>();
    expected.add(new Game(0, "player1", "player2", GameStatus.DRAW));
    when(gameFacade.getUserGames(any(), any())).thenReturn(expected);
    List<Game> result = testModel.getUserGames("player1", "cookie");
    assertEquals(result, expected);
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User not found.*")
  public void shouldNotWrapUserExceptions() throws InvalidUserCookieException, UserNotFoundException {
    when(gameFacade.getUserGames(eq("player2"), any())).thenThrow(new UserNotFoundException("User not found"));
    testModel.getUserGames("player2", "anotherCookie");
  }

  @Test (expectedExceptions = GameException.class,
          expectedExceptionsMessageRegExp = ".*Can't have a game with the same player twice.*")
  public void shouldNotWrapGameExceptions() throws GameException, UserNotFoundException, InvalidUserCookieException {
    doThrow(new GameException("Can't have a game with the same player twice")).
            when(gameFacade).createRandomGame(eq("player1"), any());
    testModel.createRandomGame("player1:cookie");
  }

  @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
  public void shouldNotAllowActionWithoutValidCookie() throws GameException, UserNotFoundException, InvalidUserCookieException {
    doThrow(new InvalidUserCookieException("Invalid User Cookie")).when(gameFacade).createRandomGame(any(), any());
    testModel.createRandomGame("player3:cookie");
  }

  @Test
  public void shouldCreateGame() throws UserNotFoundException, InvalidUserCookieException, GameException {
    testModel.createGame("player2", "player4:cookie");
    verify(gameFacade).createGame(eq("player4"), eq("player2"), any());
  }
}
