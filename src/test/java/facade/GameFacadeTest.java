package facade;

import dao.GameDao;
import exception.game.GameException;
import exception.user.InvalidUserCookieException;
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

  private List<Game> games;

  private GameFacade testModel;

  @BeforeMethod
  public void setUp() throws GameException {
    MockitoAnnotations.initMocks(this);
    testModel = new GameFacade(mockGameDao);

    games = new ArrayList<>();

    Game game0 = new Game(0, "player1", "player2", GameStatus.PLAYING);
    Game game1 = new Game(1, "player1", "pamela", GameStatus.PLAYING);
    Game game2 = new Game(2, "player2", "player4", GameStatus.PLAYING);
    Game game3 = new Game(3, "player2", "player1", GameStatus.PLAYING);

    games.add(game0);
    games.add(game1);

    when(mockGameDao.getUserGames("player1")).thenReturn(games);
    when(mockGameDao.getGame(0)).thenReturn(game0);
    when(mockGameDao.getGame(1)).thenReturn(game1);
    when(mockGameDao.getGame(2)).thenReturn(game2);
    when(mockGameDao.createGame("player1", "player2")).thenReturn(game0);
    when(mockGameDao.createGame("player1", "pamela")).thenReturn(game1);
    when(mockGameDao.createGame("player2", "player4")).thenReturn(game2);
    when(mockGameDao.createGame("player2", "player1")).thenReturn(game3);
  }

  @Test
  public void shouldReturnCorrectGamesForUser() {
    List<Game> result = testModel.getUserGames("player1", "cookie");

    assertEquals(result, games);
  }

  @Test
  public void shouldCreateGameProperly() throws InvalidUserCookieException, GameException {
    testModel.createGame("player1", "player2", "cookie");
    //TODO: Refactor when sockets are added
  }

  @Test
  public void shouldAddPlayerToQueue() throws InvalidUserCookieException, GameException {
    testModel.createRandomGame("player2", "anotherCookie");
    verify(mockGameDao, never()).createGame(any(), any());
  }

  @Test
  public void shouldCreateRandomGameProperly() throws InvalidUserCookieException, GameException {
    testModel.createRandomGame("player1", "anotherCookie");
    testModel.createRandomGame("player2", "anotherCookie");
    verify(mockGameDao).createGame(eq("player2"), eq("player1"));
  }

  @Test
  public void shouldCreateMachineLearningGameProperly() throws InvalidUserCookieException, GameException {
    testModel.createMachineLearningGame("player1", "cookie");
    verify(mockGameDao).createGame(eq("player1"), eq("pamela"));
    //TODO: Refactor when sockets are added
  }
}
