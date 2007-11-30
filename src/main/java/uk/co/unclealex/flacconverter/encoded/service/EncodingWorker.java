package uk.co.unclealex.flacconverter.encoded.service;

import java.io.IOException;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;

abstract class EncodingWorker extends Thread {

	private static final Logger log = Logger.getLogger(EncodingWorker.class);
	
	private BlockingQueue<EncodingCommandBean> i_queue;
	private int i_count;
	private SortedMap<EncodingCommandBean,Throwable> i_errors;
	
	
	public EncodingWorker(BlockingQueue<EncodingCommandBean> queue, SortedMap<EncodingCommandBean, Throwable> errors) {
		super();
		i_queue = queue;
		i_errors = errors;
	}

	@Override
	public void run() {
		BlockingQueue<EncodingCommandBean> queue = getQueue();
		SortedMap<EncodingCommandBean,Throwable> errors = getErrors();
		EncodingCommandBean encodingCommandBean;
		try {
			while(!isEndOfWorkBean(encodingCommandBean = queue.take())) {
				try {
					process(encodingCommandBean);
					setCount(getCount() + 1);
				}
				catch(Throwable t) {
					errors.put(encodingCommandBean, t);
					log.error(
						"Error whilst encoding " + encodingCommandBean.getFlacTrackBean().getUrl() + " to " + 
						encodingCommandBean.getEncoderBean().getExtension(),
						t);
				}
			}
		}
		catch (InterruptedException e) {
			// Do nothing
		}
	}
	
	protected abstract void process(EncodingCommandBean encodingCommandBean) throws IOException;

	protected EncodingCommandBean getEndOfWorkBean() {
		return new EncodingCommandBean();
	}
	
	protected boolean isEndOfWorkBean(EncodingCommandBean encodingCommandBean) {
		return
			encodingCommandBean.getEncoderBean() == null &&
			encodingCommandBean.getFlacTrackBean() == null;
	}
	
	public int getCount() {
		return i_count;
	}
	public void setCount(int count) {
		i_count = count;
	}
	public BlockingQueue<EncodingCommandBean> getQueue() {
		return i_queue;
	}
	public void setQueue(BlockingQueue<EncodingCommandBean> queue) {
		i_queue = queue;
	}

	public SortedMap<EncodingCommandBean, Throwable> getErrors() {
		return i_errors;
	}

	public void setErrors(SortedMap<EncodingCommandBean, Throwable> errors) {
		i_errors = errors;
	}
}
