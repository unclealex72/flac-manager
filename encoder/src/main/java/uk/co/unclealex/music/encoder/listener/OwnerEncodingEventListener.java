package uk.co.unclealex.music.encoder.listener;

import java.util.List;
import java.util.SortedSet;

import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.TrackOwnedAction;
import uk.co.unclealex.music.encoder.action.TrackUnownedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

public class OwnerEncodingEventListener extends AbstractEncodingEventListener {

	private EncodedTrackDao i_encodedTrackDao;

	@Override
	public void ownerAdded(
			final OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException {
		OwnerCallback callback = new OwnerCallback() {
			@Override
			public void doWithOwners(SortedSet<OwnerBean> ownerBeans) throws EventException {
				ownerBeans.add(ownerBean);
			}
		};
		execute(
				callback, encodedTrackBean, encodingActions, 
				new TrackOwnedAction(ownerBean, encodedTrackBean));
	}
	
	@Override
	public void ownerRemoved(
			final OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException {
		OwnerCallback callback = new OwnerCallback() {
			@Override
			public void doWithOwners(SortedSet<OwnerBean> ownerBeans) throws EventException {
				ownerBeans.remove(ownerBean);
			}
		};
		execute(
				callback, encodedTrackBean, encodingActions, 
				new TrackUnownedAction(ownerBean, encodedTrackBean));
	}

	protected interface OwnerCallback {
		public void doWithOwners(SortedSet<OwnerBean> ownerBeans) throws EventException;
	}
	
	protected void execute(
			OwnerCallback callback, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions, EncodingAction encodingAction) throws EventException {
		SortedSet<OwnerBean> ownerBeans = encodedTrackBean.getOwnerBeans();
		callback.doWithOwners(ownerBeans);
		getEncodedTrackDao().store(encodedTrackBean);
		encodingActions.add(encodingAction);
	}
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
	
}
