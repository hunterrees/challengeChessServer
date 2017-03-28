package api;

import exception.user.UserNotFoundException;
import manager.ClientConnectionManager;
import manager.EncryptionManager;
import model.ConnectionInfo;
import model.DHParams;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class EntryPointTest {

  private EntryPoint testModel;

  @Mock
  private ClientConnectionManager mockClientConnectionManager;
  @Mock
  private EncryptionManager mockEncryptionManager;

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    testModel = new EntryPoint(mockClientConnectionManager, mockEncryptionManager);
  }

  @Test
  public void shouldCreateConnection() {
    testModel.setUpConnection("user1", new ConnectionInfo("host", 0));
    verify(mockClientConnectionManager).setUpConnection(eq("user1"), any());
  }

  @Test
  public void shouldCreatePublicParameters() {
    testModel.sendParameters("user1");
    verify(mockEncryptionManager).getPublicParameters(eq("user1"));
  }

  @Test
  public void shouldSetUpSharedKey() throws UserNotFoundException {
    testModel.setUpSharedKey("user1", new DHParams(new DHParameterSpec(BigInteger.valueOf(5),
            BigInteger.valueOf(5)), new byte[16]));
    verify(mockEncryptionManager).generateSharedKey(eq("user1"), any());
  }
}
