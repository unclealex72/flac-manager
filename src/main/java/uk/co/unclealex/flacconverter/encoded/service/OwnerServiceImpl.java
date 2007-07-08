package uk.co.unclealex.flacconverter.encoded.service;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;
import uk.co.unclealex.flacconverter.encoded.transformer.FlacAlbumToEncodedTracksTransformer;
import uk.co.unclealex.flacconverter.encoded.transformer.ToEncodedTracksTransformerFactory;
import uk.co.unclealex.flacconverter.flac.dao.FlacAlbumDao;
import uk.co.unclealex.flacconverter.flac.dao.FlacArtistDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;

public class OwnerServiceImpl implements OwnerService {

	private FlacArtistDao i_flacArtistDao;
	private FlacAlbumDao i_flacAlbumDao;
	private EncodedTrackDao i_encodedTrackDao;
	private ToEncodedTracksTransformerFactory i_toEncodedTracksTransformerFactory;
	
	@Override
	public SortedSet<FlacAlbumBean> getOwnedAlbums(OwnerBean ownerBean) {
		ArtistTransformer artistTransformer = new ArtistTransformer();
		AlbumTransformer albumTransformer = new AlbumTransformer();
		
		SortedSet<FlacAlbumBean> flacAlbumBeans = new TreeSet<FlacAlbumBean>();
		for (OwnedArtistBean ownedArtistBean : ownerBean.getOwnedArtistBeans()) {
			flacAlbumBeans.addAll(artistTransformer.transform(ownedArtistBean));
		}
		CollectionUtils.collect(ownerBean.getOwnedAlbumBeans(), albumTransformer, flacAlbumBeans);
		return flacAlbumBeans;
	}

	protected class ArtistTransformer implements Transformer<OwnedArtistBean, SortedSet<FlacAlbumBean>> {
		@Override
		public SortedSet<FlacAlbumBean> transform(OwnedArtistBean ownedArtistBean) {
			return getFlacArtistDao().findByCode(ownedArtistBean.getName()).getFlacAlbumBeans();
		}
	}

	protected class AlbumTransformer implements Transformer<OwnedAlbumBean, FlacAlbumBean> {
		@Override
		public FlacAlbumBean transform(OwnedAlbumBean ownedAlbumBean) {
			return getFlacAlbumDao().findByArtistAndAlbum(ownedAlbumBean.getArtistName(), ownedAlbumBean.getAlbumName());
		}
	}
	
	@Override
	public SortedSet<EncodedTrackBean> getOwnedEncodedTracks(OwnerBean ownerBean, EncoderBean encoderBean) {
		if (ownerBean.isOwnsAll()) {
			return getEncodedTrackDao().findByEncoderBean(encoderBean);
		}
		FlacAlbumToEncodedTracksTransformer transformer =
			getToEncodedTracksTransformerFactory().createFlacAlbumToEncodedTracksTransformer(encoderBean);
		SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
		for (FlacAlbumBean flacAlbumBean : getOwnedAlbums(ownerBean)) {
			encodedTrackBeans.addAll(transformer.transform(flacAlbumBean));
		}
		return encodedTrackBeans;
	}
	
	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}

	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public ToEncodedTracksTransformerFactory getToEncodedTracksTransformerFactory() {
		return i_toEncodedTracksTransformerFactory;
	}

	public void setToEncodedTracksTransformerFactory(
			ToEncodedTracksTransformerFactory toEncodedTracksTransformerFactory) {
		i_toEncodedTracksTransformerFactory = toEncodedTracksTransformerFactory;
	}
}
