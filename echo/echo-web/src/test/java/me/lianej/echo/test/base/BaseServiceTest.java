package me.lianej.echo.test.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.lianej.echo.config.AppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public abstract class BaseServiceTest {
	protected Log log = LogFactory.getLog(getClass());

	protected BaseServiceTest() {
	}
	
}
