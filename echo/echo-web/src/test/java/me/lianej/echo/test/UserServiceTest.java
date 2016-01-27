package me.lianej.echo.test;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.junit.Test;

import me.lianej.echo.test.base.BaseServiceTest;

public class UserServiceTest extends BaseServiceTest{

	@Resource DataSource readOnlyDataSource;
	@Resource DataSource readWriteDataSource;
	@Resource SessionFactory readWriteSessionFactory;
	@Resource SessionFactory readOnlySessionFactory;
	
	
	@Test
	public void test1() throws Exception {
		log.error(((BasicDataSource)readOnlyDataSource).getUrl());
		log.error(((BasicDataSource)readOnlyDataSource).getPassword());
		log.error(((BasicDataSource)readWriteDataSource).getUrl());
		log.error(((BasicDataSource)readWriteDataSource).getPassword());
		log.error(readWriteDataSource);
		log.error(readWriteSessionFactory);
		log.error(readOnlySessionFactory);
	}
}
