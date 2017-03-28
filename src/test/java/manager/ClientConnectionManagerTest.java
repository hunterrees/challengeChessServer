package manager;

import org.testng.annotations.BeforeMethod;

public class ClientConnectionManagerTest {

  private ClientConnectionManager testModel;

  @BeforeMethod
  public void setUp() {
    testModel = new ClientConnectionManager();
  }
}
