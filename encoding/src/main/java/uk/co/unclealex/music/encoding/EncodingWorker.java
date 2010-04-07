package uk.co.unclealex.music.encoding;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;


public abstract class EncodingWorker extends Thread {

	private static final Logger log = Logger.getLogger(EncodingWorker.class);
	
	private BlockingQueue<EncodingCommand> i_queue;
	private int i_count;
	
	
	public EncodingWorker(BlockingQueue<EncodingCommand> queue) {
		super();
		i_queue = queue;
	}

	@Override
	public void run() {
		BlockingQueue<EncodingCommand> queue = getQueue();
		EncodingCommand encodingCommand;
		try {
			while(!isEndOfWorkCommand(encodingCommand = queue.take())) {
				try {
					process(encodingCommand);
					setCount(getCount() + 1);
				}
				catch(Throwable t) {
					log.error(
						"Error whilst encoding " + encodingCommand.getFlacFile() + " to " + encodingCommand.getDestinationFile(),
						t);
				}
			}
		}
		catch (InterruptedException e) {
			// Do nothing
		}
	}
	
	protected abstract void process(EncodingCommand encodingCommand) throws EncodingException;

	protected EncodingCommand getEndOfWorkCommand() {
		return new EncodingCommand(null, null, null, null);
	}
	
	protected boolean isEndOfWorkCommand(EncodingCommand encodingCommand) {
		return
			encodingCommand.getFlacFile() == null;
	}
	
	public int getCount() {
		return i_count;
	}
	public void setCount(int count) {
		i_count = count;
	}
	public BlockingQueue<EncodingCommand> getQueue() {
		return i_queue;
	}
	public void setQueue(BlockingQueue<EncodingCommand> queue) {
		i_queue = queue;
	}
}
