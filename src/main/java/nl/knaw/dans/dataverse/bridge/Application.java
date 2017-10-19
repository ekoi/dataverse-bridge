package nl.knaw.dans.dataverse.bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Class Application
 * Created by Eko Indarto
 */
@SpringBootApplication
@EnableTransactionManagement
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
