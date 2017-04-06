package facade;

import dao.GameDao;
import dao.MoveDao;
import dao.UserDao;
import exception.game.GameException;
import exception.game.GameNotFoundException;
import exception.game.InvalidGameCookieException;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import exception.user.UserNotFoundException;
import manager.ClientConnectionManager;
import manager.CookieManager;
import model.Move;
import model.User;
import model.UserInfo;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class MoveFacadeTest {
    private MoveFacade testMoveFacade;

    private User user1;
    private User user2;
    private User newUser;
    private User oldUser;
    private User incompleteUser;

    private Move move1;
    private Move move2;
    private Move move3;
    private List<Move> moves;

    @Mock
    private MoveDao mockMoveDao;
    @Mock
    private GameDao mockGameDao;
    @Mock
    private CookieManager mockCookieManager;
    @Mock
    private ClientConnectionManager mockClientConnectionManager;

    @BeforeMethod
    public void setUp() throws GameException, InvalidUserCookieException, UserNotFoundException {
        MockitoAnnotations.initMocks(this);
        List<User> users = new ArrayList<>();
        moves = new ArrayList<>();
        user1 = new User("user1", "password1", "email1");
        user2 = new User("user2", "password2", "email2");

        users.add(user1);
        users.add(user2);

        moves.add(move1);
        moves.add(move2);
        moves.add(move3);



        when(mockMoveDao.getMovesForGame(1)).thenReturn(moves);

        when(mockCookieManager.getQualifier("1:"+any())).thenReturn("1");
        doThrow(new InvalidUserCookieException("Invalid User Cookie")).when(mockCookieManager).validateCookies("user1:BADCOOKIE", "1:BADCOOKIE");
        doThrow(new InvalidGameCookieException("Invalid Game Cookie")).when(mockCookieManager).validateCookies("user1:GOODCOOKIE", "1:BADCOOKIE");

        testMoveFacade = new MoveFacade(mockClientConnectionManager, mockMoveDao, mockCookieManager, mockGameDao);

    }

    //getAllUsers

    @Test
    public void getAllMovesForGame() throws UserNotFoundException, InvalidUserCookieException, InvalidGameCookieException, GameNotFoundException {
        List<Move> result = testMoveFacade.getMovesForGame(1, "user1:GOODCOOKIE", "1:GOODCOOKIE");
        verify(mockCookieManager).validateCookies("user1:GOODCOOKIE", "1:GOODCOOKIE");
        verify(mockMoveDao).getMovesForGame(1);
        assertEquals(result, moves);

    }

    /*@Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void getMovesBadUserCookie() throws InvalidUserCookieException, UserNotFoundException, GameNotFoundException, InvalidGameCookieException {
        List<Move> result = testMoveFacade.getMovesForGame(1, "user1:BADCOOKIE", "1:GOODCOOKIE");
    }*/
}
