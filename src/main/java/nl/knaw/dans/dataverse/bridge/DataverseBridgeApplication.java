package nl.knaw.dans.dataverse.bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Class DataverseBridgeApplication
 * Created by Eko Indarto
 */
@SpringBootApplication
@EnableTransactionManagement
public class DataverseBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataverseBridgeApplication.class, args);
    }

}
