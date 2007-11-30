package uk.co.unclealex.flacconverter.spring;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ScheduledTimerTask extends
		org.springframework.scheduling.timer.ScheduledTimerTask {

	private static final Logger log = Logger.getLogger(ScheduledTimerTask.class);
	
	public void setNext(String next) {
		String[] parts = StringUtils.split(next, ':');
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		int seconds = Integer.parseInt(parts[2]);
		Calendar startTime = new GregorianCalendar();
		startTime.set(Calendar.HOUR_OF_DAY, hours);
		startTime.set(Calendar.MINUTE, minutes);
		startTime.set(Calendar.SECOND, seconds);
		long now = System.currentTimeMillis();
		if (startTime.getTimeInMillis() < now) {
			startTime.add(Calendar.DAY_OF_YEAR, 1);
		}
		setDelay(startTime.getTimeInMillis() - now);
		log.info(
				"Tasks have been scheduled to start running at " + 
				new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(startTime.getTime()));
	}
}
