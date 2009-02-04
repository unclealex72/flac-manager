package uk.co.unclealex.music.encoder.service;

import javax.jcr.RepositoryException;

import org.apache.log4j.Logger;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;
import uk.co.unclealex.music.core.service.filesystem.RepositoryManager;

public class RepositoryEncodingEventListener implements EncodingEventListener {

	private static final Logger log = Logger.getLogger(RepositoryEncodingEventListener.class);
	
	private RepositoryManager i_encodedRepositoryManager;
	
	@Override
	public void afterTrackEncoded(final EncodedTrackBean encodedTrackBean, FlacTrackBean flacTrackBean) {
		// Do nothing
	}

	@Override
	public void beforeTrackRemoved(final EncodedTrackBean encodedTrackBean) {
		RepositoryManagerCallback callback = new RepositoryManagerCallback() {
			@Override
			public void execute(RepositoryManager repositoryManager) throws RepositoryException {
				repositoryManager.remove(encodedTrackBean.getId());
			}
		};
		doInRepositoryManager(callback);
	}

	protected void doInRepositoryManager(RepositoryManagerCallback callback) {
		try {
			callback.execute(getEncodedRepositoryManager());
		}
		catch (RepositoryException e) {
			log.warn("Updating the repository failed.", e);
		}
	}
	protected interface RepositoryManagerCallback {
		
		public void execute(RepositoryManager repositoryManager) throws RepositoryException;
		
	}
	
	public RepositoryManager getEncodedRepositoryManager() {
		return i_encodedRepositoryManager;
	}

	public void setEncodedRepositoryManager(
			RepositoryManager encodedRepositoryManager) {
		i_encodedRepositoryManager = encodedRepositoryManager;
	}

}
