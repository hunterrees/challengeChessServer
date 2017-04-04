package manager;


import dao.GameDao;
import dao.UserDao;
import exception.game.GameNotFoundException;
import exception.game.InvalidGameCookieException;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import exception.user.UserNotFoundException;
import model.Game;
import model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;


public class CookieManagerTest {

    private CookieManager testCookieManager;

    @Mock
    private UserDao mockUserDAO;
    @Mock
    private GameDao mockGameDAO;

    @BeforeMethod
    public void setUp() throws UserException, GameNotFoundException {
        MockitoAnnotations.initMocks(this);
        List<User> users = new ArrayList<>();
        User user1 = new User("user1", "password1", "email1");
        User user2 = new User("user2", "password2", "email2");

        users.add(user1);
        users.add(user2);

        when(mockUserDAO.getAllUsers()).thenReturn(users);
        when(mockUserDAO.getUser("user1")).thenReturn(user1);
        when(mockUserDAO.getUser("user2")).thenReturn(user2);


        Game game1 = new Game(1, "player1", "player2", null);
        Game game2 = new Game(2, "player2", "player3", null);

        when(mockGameDAO.getGame(1)).thenReturn(game1);
        when(mockGameDAO.getGame(2)).thenReturn(game2);

        testCookieManager = new CookieManager(mockUserDAO, mockGameDAO);
    }

    @Test
    public void shouldMakeMatchingCookie() throws UserNotFoundException {
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        String user1Cookie2 = testCookieManager.makeUserCookie("user1");
        assertEquals(user1Cookie, user1Cookie2);
    }

    @Test
    public void shouldMakeNonMatchingCookie() throws UserNotFoundException{
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        String user2Cookie = testCookieManager.makeUserCookie("user2");
        assertNotEquals(user1Cookie, user2Cookie);
    }

    @Test
    public void shouldValidateCookie() throws UserNotFoundException, InvalidUserCookieException {
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        assertTrue(testCookieManager.validateUserCookie(user1Cookie));
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void shouldNotValidateCookieGoodUserName() throws InvalidUserCookieException, UserNotFoundException {
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        user1Cookie += "badStuff";
        testCookieManager.validateUserCookie(user1Cookie);
    }

    @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
    public void shouldNotValidateCookieBadUserName() throws InvalidUserCookieException, UserNotFoundException {
        doThrow(new UserNotFoundException("User Not Found")).when(mockUserDAO).getUser("user5");
        testCookieManager.validateUserCookie("user5:blahblahblah");
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void shouldNotValidateCookieBadUserNameNoColon() throws InvalidUserCookieException, UserNotFoundException {
        testCookieManager.validateUserCookie("user5blahblahblah");
    }

    @Test
    public void shouldMakeMatchingGameCookie() throws GameNotFoundException {
        String cookie1 = testCookieManager.makeGameCookie(1);
        String cookie2 = testCookieManager.makeGameCookie(1);
        assertEquals(cookie1, cookie2);
    }

    @Test
    public void shouldMakeNonMatchingGameCookie() throws GameNotFoundException {
        String cookie1 = testCookieManager.makeGameCookie(1);
        String cookie2 = testCookieManager.makeGameCookie(2);
        assertNotEquals(cookie1, cookie2);
    }

    @Test (expectedExceptions = InvalidGameCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid Game Cookie.*")
    public void shouldNotValidateCookie() throws InvalidGameCookieException, GameNotFoundException {
        testCookieManager.validateGameCookie("1:blahblahblah");
    }
}
