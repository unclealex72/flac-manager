package uk.co.unclealex.music.core.service;

import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import uk.co.unclealex.music.base.service.CommandBean;


public abstract class CommandWorker<E extends CommandBean<E>> extends Thread {

	private static final Logger log = Logger.getLogger(CommandWorker.class);
	
	private BlockingQueue<E> i_queue;
	private int i_count;
	private SortedMap<E,Throwable> i_errors;
	
	
	public CommandWorker(BlockingQueue<E> queue, SortedMap<E, Throwable> errors) {
		super();
		i_queue = queue;
		i_errors = errors;
	}

	@Override
	public void run() {
		BlockingQueue<E> queue = getQueue();
		SortedMap<E,Throwable> errors = getErrors();
		E commandBean;
		try {
			while(!(commandBean = queue.take()).isEndOfWorkBean()) {
				try {
					process(commandBean);
					setCount(getCount() + 1);
				}
				catch(Throwable t) {
					errors.put(commandBean, t);
					log.error(
						"Error whilst processing " + commandBean, t);
				}
			}
		}
		catch (InterruptedException e) {
			// Do nothing
		}
	}
	
	protected abstract void process(E commandBean) throws Exception;

	public int getCount() {
		return i_count;
	}
	
	public void setCount(int count) {
		i_count = count;
	}
	
	public BlockingQueue<E> getQueue() {
		return i_queue;
	}
	
	public void setQueue(BlockingQueue<E> queue) {
		i_queue = queue;
	}

	public SortedMap<E, Throwable> getErrors() {
		return i_errors;
	}

	public void setErrors(SortedMap<E, Throwable> errors) {
		i_errors = errors;
	}
}
