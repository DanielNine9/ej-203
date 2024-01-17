package huydqpc07859.ecommerce;

import huydqpc07859.ecommerce.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@AllArgsConstructor
@CrossOrigin( origins = {"http://localhost:3000"})
public class EcommerceApplication {


	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);

	}

}
