package uk.co.unclealex.music.commands;

import java.util.List;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class Main {

	public abstract void execute() throws Exception;
	
	protected abstract List<String> getContextLocations();
	
	public static void execute(Main main) throws Exception {
		ApplicationContext applicationContext =
			new ClassPathXmlApplicationContext(main.getContextLocations().toArray(new String[0]));
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(
				main, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
		main.execute();
	}
}
