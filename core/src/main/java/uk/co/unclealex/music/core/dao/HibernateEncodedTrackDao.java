package uk.co.unclealex.music.core.dao;

import java.util.Set;
import java.util.SortedSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.OwnerBean;

@Transactional
public class HibernateEncodedTrackDao extends
		HibernateKeyedDao<EncodedTrackBean> implements EncodedTrackDao {

	@Override
	public Set<EncodedTrackBean> getAllOrphanedTracks(SortedSet<String> allFlacUrls) {
		Query query =
			getSession().createQuery("from encodedTrackBean where url not in (:allFlacUrls)").
			setParameterList("allFlacUrls", allFlacUrls);
		return asSortedSet(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> findByAlbumAndEncoderCoverSupported(
			EncodedAlbumBean encodedAlbumBean, boolean coverSupported) {
		Query query =
			getSession().createQuery(
					"from encodedTrackBean where encodedAlbumBean = :encodedAlbumBean and encoderBean.coverSupported = :coverSupported");
		query.setEntity("encodedAlbumBean", encodedAlbumBean);
		query.setBoolean("coverSupported", coverSupported);
		return asSortedSet(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> getAllWithOwners() {
		Query query = getSession().createQuery("from encodedTrackBean e left join fetch e.ownerBeans");
		return asSortedSet(query);
	}
	
	@Override
	public EncodedTrackBean findByArtistCodeAndAlbumCodeAndCode(String artistCode, String albumCode, String trackCode) {
			Query query = getSession().createQuery(
					"from encodedTrackBean where code = :trackCode and " +
					"encodedAlbumBean.code = :albumCode and encodedAlbumBean.encodedArtistBean.code = :artistCode");
			query.setEntity("trackCode", trackCode);
			query.setEntity("albumCode", albumCode);
			query.setEntity("artistCode", artistCode);
			return uniqueResult(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> findByAlbumAndEncoderBean(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean) {
		Query query = getSession().createQuery(
				"from encodedTrackBean where encodedAlbumBean = :encodedAlbumBean and encoderBean = :encoderBean");
		query.setEntity("encodedAlbumBean", encodedAlbumBean);
		query.setEntity("encoderBean", encoderBean);
		return asSortedSet(query);
	}
	
	@Override
	public EncodedTrackBean findByAlbumAndEncoderBeanAndTrackNumber(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean,
			int trackNumber) {
		Query query = getSession().createQuery(
			"from encodedTrackBean " +
			"where encodedAlbumBean = :encodedAlbumBean and encoderBean = :encoderBean and trackNumber = :trackNumber");
		query.setEntity("encodedAlbumBean", encodedAlbumBean);
		query.setEntity("encoderBean", encoderBean);
		query.setInteger("trackNumber", trackNumber);
		return uniqueResult(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> findByArtistAndEncoderBean(
			EncodedArtistBean encodedArtistBean, EncoderBean encoderBean) {
		Query query = getSession().createQuery(
			"select tr from encodedAlbumBean al join al.encodedTrackBeans tr " +
			"where al.encodedArtistBean = :encodedArtistBean and tr.encoderBean = :encoderBean");
		query.setEntity("encodedArtistBean", encodedArtistBean);
		query.setEntity("encoderBean", encoderBean);
		return asSortedSet(query);
	}

	@Override
	public SortedSet<EncodedTrackBean> findByArtist(EncodedArtistBean encodedArtistBean) {
		Query query = getSession().createQuery(
			"select tr from encodedAlbumBean al join al.encodedTrackBeans tr " +
			"where al.encodedArtistBean = :encodedArtistBean");
		query.setEntity("encodedArtistBean", encodedArtistBean);
		return asSortedSet(query);
	}

	@Override
	public SortedSet<EncodedTrackBean> findByOwnerBean(OwnerBean ownerBean) {
		Query query = getSession().createQuery(
			"select e from encodedTrackBean e join e.ownerBeans o where o = :ownerBean");
		query.setEntity("ownerBean", ownerBean);
		return asSortedSet(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> findTracksWithoutAnAlbum() {
		Query query = getSession().createQuery("from encodedTrackBean where encodedAlbumBean is null");
		return asSortedSet(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> findByUrl(String url) {
		Query query = getSession().createQuery("from encodedTrackBean where flacUrl = :url").setString("url", url);
		return asSortedSet(query);
	}
	
	@Override
	public SortedSet<EncodedTrackBean> findTracksEncodedAfter(long lastSyncTimestamp, OwnerBean ownerBean,
			EncoderBean encoderBean) {
		Query query = getSession().createQuery(
			"select e from encodedTrackBean e join e.ownerBeans o " +
			"where o = :ownerBean and e.encoderBean = :encoderBean and e.timestamp > :timestamp");
		query.setEntity("ownerBean", ownerBean);
		query.setEntity("encoderBean", encoderBean);
		query.setLong("timestamp", lastSyncTimestamp);
		return asSortedSet(query);
	}

	@Override
	public EncodedTrackBean findByCodesAndEncoderAndOwner(String artistCode, String albumCode, int trackNumber,
			String trackCode, OwnerBean ownerBean, EncoderBean encoderBean) {
		Query query = getSession().createQuery(
				"select e from encodedTrackBean e, e.ownerBeans o, e.encodedAlbumBean al, al.encodedArtistBean ar " +
				"where o = :ownerBean and e.encoderBean = :encoderBean and ar.code = :artistCode and al.code = : albumCode " +
				"and e.code = :trackCode and e.trackNumber = :trackNumber");
		query.setEntity("ownerBean", ownerBean);
		query.setEntity("encoderBean", encoderBean);
		query.setString("artistCode", artistCode);
		query.setString("albumCode", albumCode);
		query.setString("artistCode", artistCode);
		query.setInteger("trackNumber", trackNumber);
		query.setString("trackCode", trackCode);
		return uniqueResult(query);
	}
	
	public EncodedTrackBean findByUrlAndEncoderBean(String url, EncoderBean encoderBean) {
		EncodedTrackBean exampleBean = createExampleBean();
		exampleBean.setFlacUrl(url);
		Criteria criteria = createFindByEncoderBean(exampleBean, encoderBean);
		return uniqueResult(criteria);
	}
		
	protected Criteria createFindByEncoderBean(EncodedTrackBean exampleBean, EncoderBean encoderBean) {
		return createFindByEncoderBean(exampleBean, encoderBean, null);
	}
	protected Criteria createFindByEncoderBean(
			EncodedTrackBean exampleBean, EncoderBean encoderBean, Criterion criterion) {
		exampleBean.setEncoderBean(encoderBean);
		Criteria criteria =
			createCriteria(exampleBean);
		if (criterion != null) {
			criteria.add(criterion);
		}
		criteria.createCriteria("encoderBean").
			add(Example.create(encoderBean));
		return criteria;
	}

	@Override
	public SortedSet<EncodedTrackBean> findByEncoderBean(EncoderBean encoderBean) {
		Criteria criteria = createFindByEncoderBean(createExampleBean(), encoderBean);
		return asSortedSet(criteria);
	}

	@Override
	public void remove(EncodedTrackBean encodedTrackBean) {
		encodedTrackBean.getOwnerBeans().clear();
		store(encodedTrackBean);
		super.remove(encodedTrackBean);
	}

	@Override
	public EncodedTrackBean createExampleBean() {
		return new EncodedTrackBean();
	}
}
