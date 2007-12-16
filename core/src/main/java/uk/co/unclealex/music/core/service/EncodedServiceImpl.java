package uk.co.unclealex.music.core.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedAlbumDao;
import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.KeyedDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.KeyedBean;

@Service
@Transactional
public class EncodedServiceImpl implements EncodedService {

	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	
	@Override
	public EncodedAlbumBean findOrCreateAlbum(
			EncodedArtistBean encodedArtistBean, String identifier, String title) {
		EncodedAlbumDao encodedAlbumDao = getEncodedAlbumDao();
		EncodedAlbumBean encodedAlbumBean = encodedAlbumDao.findByArtistAndIdentifier(encodedArtistBean, identifier);
		if (encodedAlbumBean == null) {
			encodedAlbumBean = new EncodedAlbumBean();
			encodedAlbumBean.setEncodedArtistBean(encodedArtistBean);
			encodedAlbumBean.setTitle(title);
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
			encodedArtistBean.setIdentifier(identifier);
			encodedArtistDao.store(encodedArtistBean);
		}
		return encodedArtistBean;
	}
	
	@Override
	public int removeEmptyAlbumsAndArtists() {
		EncodedAlbumDao encodedAlbumDao = getEncodedAlbumDao();
		EncodedArtistDao encodedArtistDao = getEncodedArtistDao();
		int cnt = remove(encodedAlbumDao, encodedAlbumDao.findAllEmptyAlbums());
		remove(encodedArtistDao, encodedArtistDao.findAllEmptyArtists());
		return cnt;
	}
	
	protected <T extends KeyedBean<T>> int remove(KeyedDao<T> dao, Collection<T> beans) {
		int cnt = 0;
		for (T bean : beans) {
			dao.remove(bean);
			cnt++;
		}
		return cnt;
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
}
