package eu.clarin.cmdi.linkcheckerweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"eu.clarin.cmdi.cpa", "eu.clarin.cmdi.linkcheckerweb"})
@EnableJpaRepositories(basePackages = "eu.clarin.cmdi.cpa.repository")
@EntityScan(basePackages = "eu.clarin.cmdi.cpa.model")
public class LinkcheckerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkcheckerWebApplication.class, args);
	}

}
