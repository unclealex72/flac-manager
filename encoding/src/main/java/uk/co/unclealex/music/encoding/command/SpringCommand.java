package uk.co.unclealex.music.encoding.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class SpringCommand implements Runnable {

	private static final Logger log = Logger.getLogger(SpringCommand.class);
	
	@Override
	public void run() {
		int exitValue = 0;
		try {
			List<String> contextResources = new ArrayList<String>();
			for (String contextResource : new String[] { "common", "encoding", "covers" }) {
				contextResources.add(String.format("application-context-music-%s.xml", contextResource));
			}
			ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext(contextResources.toArray(new String[0]));
			try {
				run(ctxt);
			}
			catch (Throwable t) {
				log.error("The command errored.", t);
				exitValue = 1;
			}
			finally {
				ctxt.close();
			}
		}
		finally {
			System.exit(exitValue);
		}
	}
	
	public abstract void run(ApplicationContext applicationContext) throws Exception;
}
