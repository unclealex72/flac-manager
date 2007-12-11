package uk.co.unclealex.music.core.log;

import java.util.Formatter;

import org.apache.log4j.Logger;

public class FormattableLogger {

	private Logger i_delegate;
	
	public FormattableLogger(Logger delegate) {
		super();
		i_delegate = delegate;
	}

	public FormattableLogger(Class<?> clazz) {
		this(Logger.getLogger(clazz));
	}
	
	protected String format(String format, Object... args) {
		Formatter formatter = new Formatter();
		formatter.format(format, args);
		return formatter.toString();
	}
	
	public void info(String format, Object... args) {
		i_delegate.info(format(format, args));
	}
}
