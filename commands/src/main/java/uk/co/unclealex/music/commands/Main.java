package uk.co.unclealex.music.commands;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class Main {

	public abstract void execute() throws Exception;
	
	protected List<String> getContextLocations() {
		List<String> locations = new ArrayList<String>();
		String className = getClass().getName();
		String commandName = className.substring(className.lastIndexOf('.') + 1).toLowerCase();
		String commandApplicationContext = "applicationContext-music-commands-" + commandName + ".xml";
		if (getClass().getClassLoader().getResource(commandApplicationContext) != null) {
			locations.add(commandApplicationContext);
		}
		locations.add("classpath*:applicationContext-music-commands-jdbc-direct.xml");
		return locations;
	}
	
	public static void execute(Main main) throws Exception {
		ApplicationContext applicationContext =
			new ClassPathXmlApplicationContext(main.getContextLocations().toArray(new String[0]));
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(
				main, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
		main.execute();
	}
}
