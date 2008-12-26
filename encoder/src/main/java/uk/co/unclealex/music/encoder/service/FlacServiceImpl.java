package uk.co.unclealex.music.encoder.service;

import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.FlacArtistDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacArtistBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;
import uk.co.unclealex.music.core.service.EncodedService;

@Service
@Transactional
public class FlacServiceImpl implements FlacService {

	private EncodedService i_encodedService;
	private FlacArtistDao i_flacArtistDao;
	
	@Override
	public EncodedAlbumBean findOrCreateEncodedAlbumBean(FlacAlbumBean flacAlbumBean) {
		FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
		EncodedService encodedService = getEncodedService();
		EncodedArtistBean encodedArtistBean = 
			encodedService.findOrCreateArtist(flacArtistBean.getCode(), flacArtistBean.getName());
		return
			encodedService.findOrCreateAlbum(encodedArtistBean, flacAlbumBean.getCode(), flacAlbumBean.getTitle());
	}
	
	@Override
	public String getRootUrl() {
		SortedSet<FlacArtistBean> flacArtistBeans = getFlacArtistDao().getAll();
		FlacTrackBean first = flacArtistBeans.first().getFlacAlbumBeans().first().getFlacTrackBeans().first();
		FlacTrackBean last = flacArtistBeans.last().getFlacAlbumBeans().last().getFlacTrackBeans().last();
		int indexOfdifference = StringUtils.indexOfDifference(first.getUrl(), last.getUrl());
		return first.getUrl().substring(0, indexOfdifference);
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	@Required
	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}

}
