package uk.co.unclealex.flacconverter.mains;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class Main {

	public abstract void execute() throws Exception;
	
	public static void execute(Main main) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-commandline.xml");
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(
				main, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
		main.execute();
	}
}
