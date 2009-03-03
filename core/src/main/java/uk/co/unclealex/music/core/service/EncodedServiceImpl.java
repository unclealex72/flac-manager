package uk.co.unclealex.music.core.service;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.model.AbstractEncodedBean;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedBean;
import uk.co.unclealex.music.base.model.IdentifiableBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.FilenameService;

@Service
@Transactional
public class EncodedServiceImpl implements EncodedService {

	private static Logger log = Logger.getLogger(EncodedServiceImpl.class);

	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private EncodedTrackDao i_encodedTrackDao;
	private FilenameService i_filenameService;
	
	@Override
	public EncodedAlbumBean findOrCreateAlbum(
			EncodedArtistBean encodedArtistBean, String identifier, String title) {
		EncodedAlbumDao encodedAlbumDao = getEncodedAlbumDao();
		EncodedAlbumBean encodedAlbumBean = encodedAlbumDao.findByArtistAndIdentifier(encodedArtistBean, identifier);
		if (encodedAlbumBean == null) {
			encodedAlbumBean = new EncodedAlbumBean();
			encodedAlbumBean.setEncodedArtistBean(encodedArtistBean);
			encodedAlbumBean.setTitle(title);
			injectFilename(encodedAlbumBean);
			encodedAlbumBean.setIdentifier(identifier);
			encodedAlbumDao.store(encodedAlbumBean);
		}
		return encodedAlbumBean;
	}
	
	@Override
	public EncodedArtistBean findOrCreateArtist(String identifier, String name) {
		EncodedArtistDao encodedArtistDao = getEncodedArtistDao();
		EncodedArtistBean encodedArtistBean = encodedArtistDao.findByIdentifier(identifier);
		if (encodedArtistBean == null) {
			encodedArtistBean = new EncodedArtistBean();
			encodedArtistBean.setName(name);
			injectFilename(encodedArtistBean);
			encodedArtistBean.setIdentifier(identifier);
			encodedArtistDao.store(encodedArtistBean);
		}
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
					return encodedArtistBean.getIdentifier().charAt(0);
				}
			},
			firstLetters);
		return firstLetters;
	}
	
	@Override
	public int removeEmptyAlbumsAndArtists() {
		EncodedAlbumDao encodedAlbumDao = getEncodedAlbumDao();
		EncodedArtistDao encodedArtistDao = getEncodedArtistDao();
		int cnt = remove("album", encodedAlbumDao, encodedAlbumDao.findAllEmptyAlbums());
		remove("artist", encodedArtistDao, encodedArtistDao.findAllEmptyArtists());
		return cnt;
	}
	
	protected <T extends IdentifiableBean<T, String>> int remove(String type, KeyedDao<T> dao, Collection<T> beans) {
		int cnt = 0;
		for (T bean : beans) {
			dao.remove(bean);
			log.info("Removed " + type + " " + bean.getIdentifier());
			cnt++;
		}
		return cnt;
	}

	@Override
	public void injectFilename(EncodedBean encodedBean) {
		encodedBean.setFilename(getFilenameService().createFilename(encodedBean));
	}
	
	
	@Override
	public void updateAllFilenames() {
		updateAllFilenames(getEncodedArtistDao());
		updateAllFilenames(getEncodedAlbumDao());
		updateAllFilenames(getEncodedTrackDao());
	}
	
	public <E extends AbstractEncodedBean<E>> void updateAllFilenames(KeyedDao<E> dao) {
		for (E encodedBean : dao.getAll()) {
			String oldFilename = encodedBean.getFilename();
			injectFilename(encodedBean);
			String newFilename = encodedBean.getFilename();
			if (!newFilename.equals(oldFilename)) {
				log.info("Updating '" + oldFilename + "' to '" + newFilename + "'");
				dao.store(encodedBean);
			}
		}
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
}
