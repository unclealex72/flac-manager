package uk.co.unclealex.flacconverter.encoded.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class EncoderServiceJob extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//		try {
//			getEncoderService().encodeAll();
//		}
//		catch (AlreadyEncodingException e) {
//			throw new JobExecutionException(e);
//		}
//		catch (MultipleEncodingException e) {
//			throw new JobExecutionException(e);
//		}
//		catch (CurrentlyScanningException e) {
//			throw new JobExecutionException(e);
//		}
	}
	
}
