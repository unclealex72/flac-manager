package uk.co.unclealex.music.web.commands.main;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import uk.co.unclealex.music.commands.Command;

public class Main implements Command, ApplicationContextAware {

	private static final Logger log = Logger.getLogger(Main.class);
	
	private ApplicationContext i_applicationContext;
	private Map<String, Command> i_commandsByName;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initialise() {
		ApplicationContext applicationContext = getApplicationContext();
		Map<String, Command> commandsByName = new HashMap<String, Command>();
		for (Map.Entry<String, Command> entry : ((Map<String, Command>) applicationContext.getBeansOfType(Command.class)).entrySet()) {
			String name = entry.getKey();
			Command command = entry.getValue();
			if (!(command instanceof Main)) {
				commandsByName.put(name.toLowerCase(), command);
			}
		}
		setCommandsByName(commandsByName);
	}
	
	@Override
	public void execute(String[] args) throws Exception {
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		String command = args[0].toLowerCase();
		currentThread.setName(command);
		String[] commandArguments = new String[args.length - 1];
		if (commandArguments.length != 0) {
			System.arraycopy(args, 1, commandArguments, 0, commandArguments.length);
		}
		log.info("Executing command '" + command + "' with arguments [" + StringUtils.join(commandArguments, ", ") + "]");
		try {
			getCommandsByName().get(command).execute(commandArguments);
		}
		finally {
			log.info("Finished executing command '" + command + "' with arguments [" + StringUtils.join(commandArguments, ", ") + "]");
			currentThread.setName(threadName);
		}
	}

	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

	public Map<String, Command> getCommandsByName() {
		return i_commandsByName;
	}

	public void setCommandsByName(Map<String, Command> commandsByName) {
		i_commandsByName = commandsByName;
	}

}
