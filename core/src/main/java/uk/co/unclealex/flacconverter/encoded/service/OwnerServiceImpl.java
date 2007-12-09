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
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class OwnerServiceImpl implements OwnerService {

	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public SortedSet<EncodedTrackBean> getOwnedEncodedTracks(OwnerBean ownerBean, final EncoderBean encoderBean) {
		SortedSet<FlacTrackBean> flacTrackBeans;
		if (ownerBean.isOwnsAll()) {
			flacTrackBeans = getFlacTrackDao().getAll();
		}
		else {
			flacTrackBeans = new TreeSet<FlacTrackBean>();
			for (FlacAlbumBean flacAlbumBean : getOwnedAlbums(ownerBean)) {
				flacTrackBeans.addAll(flacAlbumBean.getFlacTrackBeans());
			}
		}
		Transformer<FlacTrackBean, String> toUrltransformer = new Transformer<FlacTrackBean, String>() {
			@Override
			public String transform(FlacTrackBean flacTrackBean) {
				return flacTrackBean.getUrl();
			}
		};
		List<String> urls = new LinkedList<String>();
		CollectionUtils.collect(flacTrackBeans, toUrltransformer, urls);
		return getEncodedTrackDao().findByUrlsAndEncoderBean(urls, encoderBean);
	}
	
	@Required
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
