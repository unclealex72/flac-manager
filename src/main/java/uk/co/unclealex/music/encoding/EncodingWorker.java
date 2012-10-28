package uk.co.unclealex.music.encoding;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class EncodingWorker implements Callable<Integer> {

	private static final Logger log = LoggerFactory.getLogger(EncodingWorker.class);
	
	private final BlockingQueue<Path> queue;
	
	public EncodingWorker(BlockingQueue<Path> queue) {
		super();
		this.queue = queue;
	}

	@Override
	public Integer call() {
		int count = 0;
		BlockingQueue<Path> queue = getQueue();
		Path path;
		try {
			while(!isEndOfWorkCommand(path = queue.take())) {
				try {
					process(path);
					count++;
				}
				catch(Exception t) {
					log.error(
						"Error whilst encoding " + path,
						t);
				}
			}
		}
		catch (InterruptedException e) {
			// Do nothing
		}
		return count;
	}
	
	protected abstract void process(Path Path) throws EncodingException;

	protected Path getEndOfWorkCommand() {
		return null;
	}
	
	protected boolean isEndOfWorkCommand(Path path) {
		return
			path == null;
	}
	
	public BlockingQueue<Path> getQueue() {
		return queue;
	}
	
}
