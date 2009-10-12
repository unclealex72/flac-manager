package uk.co.unclealex.music.core.dao;

import java.util.Set;
import java.util.SortedSet;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.model.AlbumCoverBean;

@Transactional
public class HibernateAlbumCoverDao extends
		HibernateKeyedDao<AlbumCoverBean> implements AlbumCoverDao {

	@Override
	public AlbumCoverBean createExampleBean() {
		return new AlbumCoverBean();
	}

	@Override
	public AlbumCoverBean findByUrl(String url) {
		AlbumCoverBean albumCoverBean = createExampleBean();
		albumCoverBean.setUrl(url);
		return uniqueResult(createCriteria(albumCoverBean));
	}
	
	@Override
	public SortedSet<AlbumCoverBean> getCoversForAlbum(String artistCode, String albumCode) {
		AlbumCoverBean albumCoverBean = createExampleBean();
		albumCoverBean.setAlbumCode(albumCode);
		albumCoverBean.setArtistCode(artistCode);
		return asSortedSet(createCriteria(albumCoverBean));
	}
	
	@Override
	public Set<String> findSelectedAlbumCoverFilePaths() {
		Query query = getSession().createQuery(
		"select dataBean.path from albumCoverBean where dateSelected is not null");
	return asSortedSet(query, String.class);
	}
	
	@Override
	public SortedSet<AlbumCoverBean> getSelected() {
		Query query = getSession().createQuery(
			"from albumCoverBean where dateSelected is not null");
		return asSortedSet(query);
	}

	@Override
	public AlbumCoverBean findSelectedCoverForAlbum(String artistCode, String albumCode) {
		Query query = getSession().createQuery(
			"from albumCoverBean where artistCode = :artistCode and albumCode = :albumCode and dateSelected is not null").
			setString("albumCode", albumCode).
			setString("artistCode", artistCode);
		return uniqueResult(query);
	}
	
	@Override
	public int countSelectedAlbums() {
		Query query = getSession().createQuery(
				"select count(albumCoverBean) where dateSelected is not null");
		return (Integer) query.uniqueResult();
	}
}
