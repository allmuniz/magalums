package project.allmuniz.magalums;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
		info = @Info(
				title = "Magalu",
				description = "Desafio Back-End Magalu",
				version = "1"
		)
)
public class MagalumsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MagalumsApplication.class, args);
	}

}
