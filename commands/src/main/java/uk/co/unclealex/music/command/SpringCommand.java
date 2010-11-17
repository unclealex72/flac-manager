package uk.co.unclealex.music.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class SpringCommand {

	protected static final char HELP_OPTION = 'h';
	private static final Logger log = Logger.getLogger(SpringCommand.class);
	
	public void run(String[] args) {
		int exitValue = 0;
		Options options = createOptions();
		try {
			CommandLineParser parser = new GnuParser();
			List<String> argList = new ArrayList<String>(Arrays.asList(args));
			argList.remove(0);
			CommandLine commandLine = parser.parse(options, argList.toArray(new String[0]));
			if (commandLine.hasOption('h')) {
				throw new ParseException(null);
			}
			checkCommandLine(commandLine);
			List<String> contextResources = new ArrayList<String>();
			for (String contextResource : new String[] { "common", "encoding", "covers", "sync" }) {
				contextResources.add(String.format("classpath*:application-context-music-%s.xml", contextResource));
			}
			ClassPathXmlApplicationContext ctxt = null;
			try {
				ctxt = new ClassPathXmlApplicationContext(contextResources.toArray(new String[0]));
				run(ctxt, commandLine);
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
		catch (ParseException e) {
			exitValue = 2;
			HelpFormatter formatter = new HelpFormatter();
			String message = e.getMessage();
			if (!StringUtils.isEmpty(message)) {
				System.out.println(message);
			}
			formatter.printHelp(args[0], options, true);
		}
		catch (Throwable t) {
			log.error("The command errored.", t);
			exitValue = 1;
		}
		finally {
			System.exit(exitValue);
		}
	}
	
	protected void checkCommandLine(CommandLine commandLine) throws ParseException {
	}

	protected Options createOptions() {
		Options options = new Options();
		@SuppressWarnings("static-access")
		Option helpOption = OptionBuilder.withDescription("Print this message.").create(HELP_OPTION);
		options.addOption(helpOption);
		for (Option option : addOptions()) {
			options.addOption(option);
		}
		return options;
	}

	protected Option[] addOptions() {
		return new Option[0];
	}
	
	protected abstract void run(ApplicationContext applicationContext, CommandLine commandLine) throws Exception;
}
