package facade;


import dao.UserDAO;


import exception.user.UserException;
import exception.user.UserNotFoundException;
import model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;


import static org.mockito.Mockito.when;


public class CookieManagerTest {

    private CookieManager testCookieManager;

    private List<User> users;
    private User user1;
    private User user2;
    private User user3;

    @Mock
    private UserDAO mockUserDAO;

    @BeforeMethod
    public void setUp() throws UserException {
        MockitoAnnotations.initMocks(this);
        users = new ArrayList<>();
        user1 = new User("user1", "password1", "email1");
        user2 = new User("user2", "password2", "email2");
        user3 = new User("user3", "password3", "email3");

        users.add(user1);
        users.add(user2);


        when(mockUserDAO.getAllUsers()).thenReturn(users);
        when(mockUserDAO.getUser("user1")).thenReturn(user1);
        when(mockUserDAO.getUser("user2")).thenReturn(user2);


        testCookieManager = new CookieManager(mockUserDAO);
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



}
