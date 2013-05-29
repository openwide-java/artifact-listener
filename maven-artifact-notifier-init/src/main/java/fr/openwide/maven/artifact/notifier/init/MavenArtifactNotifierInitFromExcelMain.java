package fr.openwide.maven.artifact.notifier.init;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.init.config.spring.MavenArtifactNotifierInitConfig;
import fr.openwide.maven.artifact.notifier.init.util.SpringContextWrapper;

public final class MavenArtifactNotifierInitFromExcelMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(MavenArtifactNotifierInitFromExcelMain.class);

	public static void main(String[] args) throws ServiceException, SecurityServiceException, IOException {
		ConfigurableApplicationContext context = null;
		try {
			context = new AnnotationConfigApplicationContext(MavenArtifactNotifierInitConfig.class);
			
			SpringContextWrapper contextWrapper = context.getBean("springContextWrapper",
					SpringContextWrapper.class);
			
			contextWrapper.openEntityManager();
			contextWrapper.importDirectory(new File("src/main/resources/init/development"));
			
			contextWrapper.reindexAll();
			
			LOGGER.info("Initialization complete");
		} finally {
			if (context != null) {
				context.close();
			}
			System.exit(0);
		}
	}
	
	private MavenArtifactNotifierInitFromExcelMain() {
	}
}
