package api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class EntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.class);

  @RequestMapping(method = RequestMethod.GET)
  String inform() {
    LOGGER.info("/ hit");
    return "Not yet implemented";
  }

  public static void main(String[] args) throws Exception {
    Object[] sources = {EntryPoint.class, UserEndpoint.class, GameEndpoint.class, MoveEndpoint.class};
    LOGGER.info("Starting server with following classes: {}", sources);
    SpringApplication.run(sources, args);
  }
}
