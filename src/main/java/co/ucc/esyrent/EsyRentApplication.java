package co.ucc.esyrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EsyRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsyRentApplication.class, args);
    }

}
