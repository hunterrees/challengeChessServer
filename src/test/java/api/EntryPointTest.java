package api;

import org.testng.annotations.BeforeMethod;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public class EntryPointTest {

  private EntryPoint testModel;

  @BeforeMethod
  public void setUp() throws NoSuchAlgorithmException, NoSuchPaddingException {
    testModel = new EntryPoint();
  }
}
