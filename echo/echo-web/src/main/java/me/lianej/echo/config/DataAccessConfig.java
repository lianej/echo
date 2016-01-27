package me.lianej.echo.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:datasource.properties")
public class DataAccessConfig {

	@Resource Environment env;
	
	@Bean DataSource readOnlyDataSource(){
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(env.getProperty("datasource.realonly.driverClassName"));
		ds.setPassword(env.getProperty("datasource.realonly.password"));
		ds.setUsername(env.getProperty("datasource.realonly.username"));
		ds.setUrl(env.getProperty("datasource.realonly.url"));
		return ds;
	}
	@Bean DataSource readWriteDataSource(){
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(env.getProperty("datasource.readwrite.driverClassName"));
		ds.setPassword(env.getProperty("datasource.readwrite.password"));
		ds.setUsername(env.getProperty("datasource.readwrite.username"));
		ds.setUrl(env.getProperty("datasource.readwrite.url"));
		return ds;
	}
	
}
