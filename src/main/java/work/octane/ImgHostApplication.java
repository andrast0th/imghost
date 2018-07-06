package work.octane;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ImgHostApplication {

	private int maxUploadSizeInMb = 25 * 1024 * 1024; // 10 MB

	public static void main(String[] args) {
		SpringApplication.run(ImgHostApplication.class, args);
	}

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbedded() {

		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

		tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
			if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
				((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(maxUploadSizeInMb);
			}
		});

		return tomcat;

	}

}