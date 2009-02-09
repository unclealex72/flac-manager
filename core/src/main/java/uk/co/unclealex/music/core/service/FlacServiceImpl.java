package uk.co.unclealex.music.core.service;

import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.FlacArtistDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.FlacService;

@Service
@Transactional
public class FlacServiceImpl implements FlacService {

	private EncodedService i_encodedService;
	private FlacArtistDao i_flacArtistDao;
	private Pattern i_albumPathPattern;
	private FlacTrackDao i_flacTrackDao;
	
	@PostConstruct
	public void initialise() {
		setAlbumPathPattern(Pattern.compile("[a-z]+?://(.+)"));
	}
	
	@Override
	public FlacAlbumBean findFlacAlbumByPath(String path) {
		if (!path.endsWith("/")) {
			path += "/"; 
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		FlacTrackBean flacTrackBean = getFlacTrackDao().findTrackStartingWith("file://" + path);
		return flacTrackBean == null?null:flacTrackBean.getFlacAlbumBean();
	}
	
	@Override
	public String getPathForFlacAlbum(FlacAlbumBean flacAlbumBean) {
		SortedSet<FlacTrackBean> flacTrackBeans = flacAlbumBean.getFlacTrackBeans();
		if (flacTrackBeans == null || flacTrackBeans.isEmpty()) {
			return null;
		}
		FlacTrackBean flacTrackBean = flacTrackBeans.first();
		return flacTrackBean.getFile().getParent();
	}

	public Pattern getAlbumPathPattern() {
		return i_albumPathPattern;
	}

	public void setAlbumPathPattern(Pattern albumPathPattern) {
		i_albumPathPattern = albumPathPattern;
	}

	
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

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	@Required
	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

}
