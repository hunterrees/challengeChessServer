package facade;

import dao.GameDao;
import dao.UserDAO;
import exception.game.GameException;
import exception.user.InvalidUserCookieException;
import model.Game;
import model.GameStatus;
import model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class GameFacadeTest {

  @Mock
  private GameDao mockGameDao;
  @Mock
  private UserDAO mockUserDao;

  private List<Game> games;
  private Game game0;
  private Game game1;

  private GameFacade testModel;

  @BeforeMethod
  public void setUp() throws GameException {
    MockitoAnnotations.initMocks(this);
    testModel = new GameFacade(mockGameDao, mockUserDao);

    games = new ArrayList<>();

    game0 = new Game(0, "player1", "player2", GameStatus.PLAYING);
    game1 = new Game(1, "player1", "pamela", GameStatus.PLAYING);
    Game game2 = new Game(2, "player2", "player4", GameStatus.PLAYING);
    Game game3 = new Game(3, "player2", "player1", GameStatus.PLAYING);

    games.add(game0);
    games.add(game1);

    List<User> users = new ArrayList<>();
    users.add(new User("pamela", "password", "email"));
    users.add(new User("player1", "password", "email"));
    users.add(new User("player2", "password", "email"));
    users.add(new User("player4", "password", "email"));

    when(mockGameDao.getUserGames("player1")).thenReturn(games);
    when(mockGameDao.getGame(0)).thenReturn(game0);
    when(mockGameDao.getGame(1)).thenReturn(game1);
    when(mockGameDao.getGame(2)).thenReturn(game2);
    when(mockGameDao.createGame("player1", "player2")).thenReturn(game0);
    when(mockGameDao.createGame("player1", "pamela")).thenReturn(game1);
    when(mockGameDao.createGame("player2", "player4")).thenReturn(game2);
    when(mockGameDao.createGame("player2", "player1")).thenReturn(game3);
    when(mockUserDao.hasUser("player1")).thenReturn(true);
    when(mockUserDao.hasUser("player2")).thenReturn(true);
    when(mockUserDao.hasUser("player4")).thenReturn(true);
    when(mockUserDao.hasUser("pamela")).thenReturn(true);
    when(mockUserDao.getAllUsers()).thenReturn(users);
  }

  @Test
  public void shouldReturnCorrectGamesForUser() {
    List<Game> result = testModel.getUserGames("player1");

    assertEquals(result, games);
  }

  @Test
  public void shouldCreateGameProperly() throws InvalidUserCookieException, GameException {
    Game result = testModel.createGame("player1", "player2", "cookie");

    assertEquals(result, game0);
  }

  @Test
  public void shouldCreateRandomGameProperly() throws InvalidUserCookieException, GameException {
    Game result = testModel.createRandomGame("player2", "anotherCookie");

    assertEquals(result.getPlayer1(), "player2");
    assertNotEquals(result.getPlayer2(), "player2");
    assertNotEquals(result.getPlayer2(), "pamela");
  }

  @Test
  public void shouldCreateMachineLearningGameProperly() throws InvalidUserCookieException, GameException {
    Game result = testModel.createMachineLearningGame("player1", "cookie");

    assertEquals(result, game1);
  }
}
