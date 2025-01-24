package ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

	@Bean
	OpenAPI sgecommerceOpenAPI() {

		Server localServer = new Server();
		localServer.setUrl("http://localhost:8080");
		localServer.setDescription("Server URL in Local environment");

		Info info = new Info().title("Ecommerce API").version("1.0").description("""
				    Essa API expõe um serviço de Ecommerce.

				    - Gabriel Ribeiro: gabriel.ribeiro.099@ufrn.edu.br
				    - Pablo Paiva: pablo.paiva.123@ufrn.edu.br
				    - Pablo Gustavo: pablopgfm@gmail.com
				""");

		OpenAPI api = new OpenAPI().info(info).addServersItem(localServer);

		return api;
	}
}
