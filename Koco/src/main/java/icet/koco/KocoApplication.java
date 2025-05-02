package icet.koco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class KocoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KocoApplication.class, args);
	}

}
