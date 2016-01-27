package me.lianej.echo.config;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages="me.lianej.echo")
@Import(SessionFactroyConfig.class)
public class AppConfig {
	
	@PostConstruct void init(){
		LogFactory factory = LogFactory.getFactory();
		factory.setAttribute("org.apache.commons.logging.Log", "org.apache.logging.log4j.core.Logger");
	}

}
