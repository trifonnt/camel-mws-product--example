package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author Trifon Trifonov
 */
@SpringBootApplication
public class MyApplication {

	private static final Logger LOG = LoggerFactory.getLogger(MyApplication.class);


	/**
	 * @param args
	 */
	public static void main(String... args) {
		LOG.info(" *** Starting Spring Boot Camel Demo Application ***");
		SpringApplication.run(MyApplication.class, args);
	}
}