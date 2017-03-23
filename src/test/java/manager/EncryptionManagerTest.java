package manager;

import exception.user.UserNotFoundException;
import model.DHParams;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class EncryptionManagerTest {

  private EncryptionManager testModel;

  @BeforeMethod
  public void setUp() throws NoSuchPaddingException, NoSuchAlgorithmException {
    testModel = new EncryptionManager();
  }

  @Test
  public void shouldSendPublicParametersBack() {
    DHParams result = testModel.getParameters("test1");

    assertEquals(result.getParams().getG(), BigInteger.valueOf(5));
    assertEquals(result.getParams().getP().bitLength(), 500);
  }

  @Test
  public void shouldGenerateDifferentPublicParametersForEachUser() {
    DHParams test1 = testModel.getParameters("test1");
    DHParams test2 = testModel.getParameters("test2");

    assertNotEquals(test1.getPublicParam(), test2.getPublicParam());
  }

  @Test
  public void shouldNotOverrideParameterForUserIfCalledAgain() {
    DHParams result = testModel.getParameters("test1");
    DHParams result2 = testModel.getParameters("test1");

    assertEquals(result.getPublicParam(), result2.getPublicParam());
  }

  @Test
  public void shouldGenerateSameSharedKey() throws UserNotFoundException {
    DHParams result = testModel.getParameters("test1");
    BigInteger a = BigInteger.probablePrime(500, new SecureRandom());
    BigInteger publicKey = testModel.modExp(result.getParams().getG(), a, result.getParams().getP());

    BigInteger expectedSharedKey = testModel.modExp(new BigInteger(result.getPublicParam()), a, result.getParams().getP());
    testModel.setUpSharedKey("test1", new DHParams(result.getParams(), publicKey.toByteArray()));

    byte[] newSharedKey = new byte[16];
    for (int i = 0; i < 16; i++) {
      newSharedKey[i] = expectedSharedKey.toByteArray()[i];
    }

    assertEquals(Arrays.toString(newSharedKey), Arrays.toString(testModel.sharedKeys.get("test1")));
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*initial parameters.*")
  public void shouldThrowErrorWhenSharedKeyIsAttemptedWithoutInitialization() throws UserNotFoundException {
    DHParameterSpec params = new DHParameterSpec(BigInteger.valueOf(5), BigInteger.valueOf(7));
    testModel.setUpSharedKey("test2", new DHParams(params, new byte[2]));
  }
}
