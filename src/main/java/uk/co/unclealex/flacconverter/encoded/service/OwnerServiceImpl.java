package uk.co.unclealex.flacconverter.encoded.service;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;
import uk.co.unclealex.flacconverter.flac.dao.FlacAlbumDao;
import uk.co.unclealex.flacconverter.flac.dao.FlacArtistDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class OwnerServiceImpl implements OwnerService {

	private FlacArtistDao i_flacArtistDao;
	private FlacAlbumDao i_flacAlbumDao;
	private EncodedTrackDao i_encodedTrackDao;
	
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
		List<String> urls = new LinkedList<String>();
		Transformer<FlacTrackBean, String> transformer = new Transformer<FlacTrackBean, String>() {
			@Override
			public String transform(FlacTrackBean flacTrackBean) {
				return flacTrackBean.getUrl();
			}
		};
		for (FlacAlbumBean flacAlbumBean : getOwnedAlbums(ownerBean)) {
			CollectionUtils.collect(flacAlbumBean.getFlacTrackBeans(), transformer, urls);
		}
		return getEncodedTrackDao().findByUrlsAndEncoderBean(urls, encoderBean);
	}
	
	@Required
	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}

	@Required
	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}

	@Required
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

}
