package uk.co.unclealex.music.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class SpringCommand {

	private static final Logger log = Logger.getLogger(SpringCommand.class);
	
	public void run(String[] args) {
		int exitValue = 0;
		try {
			List<String> contextResources = new ArrayList<String>();
			for (String contextResource : new String[] { "common", "encoding", "covers", "sync" }) {
				contextResources.add(String.format("classpath*:application-context-music-%s.xml", contextResource));
			}
			ClassPathXmlApplicationContext ctxt = null;
			try {
				ctxt = new ClassPathXmlApplicationContext(contextResources.toArray(new String[0]));
				run(ctxt, args);
			}
			catch (Throwable t) {
				log.error("The command errored.", t);
				exitValue = 1;
			}
			finally {
				if (ctxt != null) {
					ctxt.close();
				}
			}
		}
		finally {
			System.exit(exitValue);
		}
	}
	
	public abstract void run(ApplicationContext applicationContext, String[] args) throws Exception;
}
