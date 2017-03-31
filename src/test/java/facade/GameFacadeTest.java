package facade;

import dao.GameDao;
import exception.game.GameException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import manager.ClientConnectionManager;
import manager.CookieManager;
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

public class GameFacadeTest {

  @Mock
  private GameDao mockGameDao;
  @Mock
  private ClientConnectionManager mockClientConnectionManager;
  @Mock
  private CookieManager mockCookieManager;

  private List<Game> games;

  private GameFacade testModel;

  @BeforeMethod
  public void setUp() throws GameException {
    MockitoAnnotations.initMocks(this);
    testModel = new GameFacade(mockGameDao, mockCookieManager, mockClientConnectionManager);

    games = new ArrayList<>();

    Game game0 = new Game(0, "player1", "player2", GameStatus.PLAYING);
    Game game1 = new Game(1, "player1", "pamela", GameStatus.PLAYING);
    Game game2 = new Game(2, "player2", "player4", GameStatus.PLAYING);
    Game game3 = new Game(3, "player2", "player1", GameStatus.PLAYING);

    games.add(game0);
    games.add(game1);

    when(mockGameDao.getUserGames("player1")).thenReturn(games);
    when(mockGameDao.createGame("player1", "player2")).thenReturn(game0);
    when(mockGameDao.createGame("player1", "pamela")).thenReturn(game1);
    when(mockGameDao.createGame("player2", "player4")).thenReturn(game2);
    when(mockGameDao.createGame("player2", "player1")).thenReturn(game3);
  }

  @Test
  public void shouldReturnCorrectGamesForUser() throws InvalidUserCookieException, UserNotFoundException {
    List<Game> result = testModel.getUserGames("player1", "cookie");

    assertEquals(result, games);
  }

  @Test
  public void shouldCreateGameProperly() throws InvalidUserCookieException, GameException, UserNotFoundException {
    testModel.createGame("player1", "player2", "cookie");
    verify(mockClientConnectionManager, times(2)).sendData(eq("player1"), any());
    verify(mockClientConnectionManager, times(2)).sendData(eq("player2"), any());
  }

  @Test
  public void shouldAddPlayerToQueue() throws InvalidUserCookieException, GameException, UserNotFoundException {
    testModel.createRandomGame("player2", "anotherCookie");
    verify(mockGameDao, never()).createGame(any(), any());
    verify(mockClientConnectionManager, never()).sendData(eq("player2"), any());
  }

  @Test
  public void shouldCreateRandomGameProperly() throws InvalidUserCookieException, GameException, UserNotFoundException {
    testModel.createRandomGame("player1", "anotherCookie");
    testModel.createRandomGame("player2", "anotherCookie");
    verify(mockGameDao).createGame(eq("player2"), eq("player1"));
    verify(mockClientConnectionManager, times(2)).sendData(eq("player1"), any());
    verify(mockClientConnectionManager, times(2)).sendData(eq("player2"), any());
  }

  @Test
  public void shouldCreateMachineLearningGameProperly() throws InvalidUserCookieException, GameException, UserNotFoundException {
    testModel.createGame("player1", "pamela", "cookie");
    verify(mockGameDao).createGame(eq("player1"), eq("pamela"));
    verify(mockClientConnectionManager, times(2)).sendData(eq("player1"), any());
    verify(mockClientConnectionManager, times(2)).sendData(eq("pamela"), any());
  }
}
