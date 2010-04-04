package uk.co.unclealex.music.core.dao;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.FlacAlbumDao;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

@Transactional
public class HibernateFlacAlbumDao extends HibernateCodeDao<FlacAlbumBean> implements
		FlacAlbumDao {

	@Override
	public FlacAlbumBean findByArtistAndAlbum(String artistCode, String albumCode) {
		Query query = getSession().createQuery("from FlacAlbumBean where code = :albumCode and flacArtistBean.code = :artistCode");
		query.setString("albumCode", albumCode);
		query.setString("artistCode", artistCode);
		return uniqueResult(query);
	}

	@Override
	public FlacAlbumBean createExampleBean() {
		return new FlacAlbumBean();
	}

}
