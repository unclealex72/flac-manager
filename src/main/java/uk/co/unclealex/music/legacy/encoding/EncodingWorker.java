package uk.co.unclealex.music.legacy.encoding;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.process.NamedCallable;


public abstract class EncodingWorker extends NamedCallable<Integer> {

	private static final Logger log = LoggerFactory.getLogger(EncodingWorker.class);
	
	private BlockingQueue<EncodingCommand> i_queue;
	private int i_index;
	
	public EncodingWorker(BlockingQueue<EncodingCommand> queue, int index) {
		super();
		i_queue = queue;
		i_index = index;
	}

	@Override
	public String getThreadName() {
		return String.format("Encoder %d", getIndex());
	}
	
	@Override
	public Integer callAfterName() {
		int count = 0;
		BlockingQueue<EncodingCommand> queue = getQueue();
		EncodingCommand encodingCommand;
		try {
			while(!isEndOfWorkCommand(encodingCommand = queue.take())) {
				try {
					process(encodingCommand);
					count++;
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
		return count;
	}
	
	protected abstract void process(EncodingCommand encodingCommand) throws EncodingException;

	protected EncodingCommand getEndOfWorkCommand() {
		return new EncodingCommand(null, null, null, null);
	}
	
	protected boolean isEndOfWorkCommand(EncodingCommand encodingCommand) {
		return
			encodingCommand.getFlacFile() == null;
	}
	
	public int getIndex() {
		return i_index;
	}
	
	public BlockingQueue<EncodingCommand> getQueue() {
		return i_queue;
	}
	
	public void setQueue(BlockingQueue<EncodingCommand> queue) {
		i_queue = queue;
	}
}
