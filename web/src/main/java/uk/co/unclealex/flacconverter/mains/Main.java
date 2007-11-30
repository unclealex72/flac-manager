package uk.co.unclealex.flacconverter.mains;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.opensymphony.xwork2.spring.SpringObjectFactory;

public abstract class Main {

	public abstract void execute() throws Exception;
	
	public static void execute(Main main) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-commandline.xml");
		SpringObjectFactory factory = new SpringObjectFactory();
		factory.setApplicationContext(applicationContext);
		factory.setAutowireStrategy(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
		factory.autoWireBean(main);
		main.execute();
	}
}
