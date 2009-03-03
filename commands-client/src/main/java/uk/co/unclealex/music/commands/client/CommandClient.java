package uk.co.unclealex.music.commands.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.unclealex.music.commands.Command;

public class CommandClient {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctxt = new ClassPathXmlApplicationContext("applicationContext-music-commands.xml");
		Command command = (Command) ctxt.getBean("command");
		command.execute(args);
	}
}
