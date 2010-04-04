package uk.co.unclealex.music.core.service;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.FilenameService;
import uk.co.unclealex.music.base.service.OwnerService;

@Transactional
public class EncodedServiceImpl implements EncodedService {

	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private EncodedTrackDao i_encodedTrackDao;
	private FlacTrackDao i_flacTrackDao;
	private FilenameService i_filenameService;
	private OwnerService i_ownerService;
	
	@Override
	public EncodedTrackBean createTrack(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean, FlacTrackBean flacTrackBean) {
		EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
		encodedTrackBean.setFlacUrl(flacTrackBean.getUrl());
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setTimestamp(new Date().getTime());
		encodedTrackBean.setTitle(flacTrackBean.getTitle());
		encodedTrackBean.setTrackNumber(flacTrackBean.getTrackNumber());
		encodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setCode(flacTrackBean.getCode());
		injectFilename(encodedTrackBean);
		encodedAlbumBean.getEncodedTrackBeans().add(encodedTrackBean);
		return encodedTrackBean;
	}
	
	@Override
	public EncodedAlbumBean createAlbum(
			EncodedArtistBean encodedArtistBean, FlacAlbumBean flacAlbumBean) {
		EncodedAlbumBean encodedAlbumBean = new EncodedAlbumBean();
		encodedAlbumBean.setEncodedArtistBean(encodedArtistBean);
		encodedAlbumBean.setTitle(flacAlbumBean.getTitle());
		injectFilename(encodedAlbumBean);
		encodedAlbumBean.setCode(flacAlbumBean.getCode());
		encodedArtistBean.getEncodedAlbumBeans().add(encodedAlbumBean);
		getEncodedAlbumDao().store(encodedAlbumBean);
		return encodedAlbumBean;
	}
	
	@Override
	public EncodedArtistBean createArtist(FlacArtistBean flacArtistBean) {
		EncodedArtistBean encodedArtistBean = new EncodedArtistBean();
		encodedArtistBean.setName(flacArtistBean.getName());
		injectFilename(encodedArtistBean);
		encodedArtistBean.setCode(flacArtistBean.getCode());
		getEncodedArtistDao().store(encodedArtistBean);
		return encodedArtistBean;
	}
	
	@Override
	public SortedSet<Character> getAllFirstLettersOfArtists() {
		SortedSet<EncodedArtistBean> encodedArtistBeans = getEncodedArtistDao().getAll();
		SortedSet<Character> firstLetters = new TreeSet<Character>();
		CollectionUtils.collect(
			encodedArtistBeans, 
			new Transformer<EncodedArtistBean, Character>() {
				@Override
				public Character transform(EncodedArtistBean encodedArtistBean) {
					return encodedArtistBean.getCode().charAt(0);
				}
			},
			firstLetters);
		return firstLetters;
	}
	
	@Override
	public Set<EncodedTrackBean> findOrphanedEncodedTrackBeans() {
		SortedSet<String> allFlacUrls = getFlacTrackDao().getAllUrls();
		return getEncodedTrackDao().getAllOrphanedTracks(allFlacUrls);
	}
	
	protected void injectFilename(EncodedBean encodedBean) {
		encodedBean.setFilename(getFilenameService().createFilename(encodedBean));
	}
	
	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}
	
	@Required
	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}
	
	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}
	
	@Required
	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public FilenameService getFilenameService() {
		return i_filenameService;
	}

	@Required
	public void setFilenameService(FilenameService filenameService) {
		i_filenameService = filenameService;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
