package me.lianej.echo.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

@Configuration
@Import(DataAccessConfig.class)
public class SessionFactroyConfig {

	@Resource DataSource readOnlyDataSource;
	@Resource DataSource readWriteDataSource;
	
	@Bean SessionFactory readOnlySessionFactory(){
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(readOnlyDataSource);
		
		
		return builder.buildSessionFactory();
	}
	
	@Bean SessionFactory readWriteSessionFactory(){
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(readWriteDataSource);
		
		
		return builder.buildSessionFactory();
	}
	
}
