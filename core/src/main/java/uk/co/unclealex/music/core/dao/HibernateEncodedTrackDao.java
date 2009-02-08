package uk.co.unclealex.music.core.dao;

import java.io.IOException;
import java.util.SortedSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.io.KnownLengthInputStreamCallback;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;

@Repository
@Transactional
public class HibernateEncodedTrackDao extends
		HibernateKeyedDao<EncodedTrackBean> implements EncodedTrackDao {

	private Streamer i_streamer;
	
	@Autowired
	public HibernateEncodedTrackDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SortedSet<? extends EncodedTrackBean> findByAlbumAndEncoderBean(
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
	public SortedSet<? extends EncodedTrackBean> findByArtistAndEncoderBean(
			EncodedArtistBean encodedArtistBean, EncoderBean encoderBean) {
		Query query = getSession().createQuery(
			"select tr from encodedAlbumBean al join al.encodedTrackBeans tr " +
			"where al.encodedArtistBean = :encodedArtistBean and tr.encoderBean = :encoderBean");
		query.setEntity("encodedArtistBean", encodedArtistBean);
		query.setEntity("encoderBean", encoderBean);
		return asSortedSet(query);
	}

@Override
	public SortedSet<? extends EncodedTrackBean> findByArtist(EncodedArtistBean encodedArtistBean) {
		Query query = getSession().createQuery(
			"select tr from encodedAlbumBean al join al.encodedTrackBeans tr " +
			"where al.encodedArtistBean = :encodedArtistBean");
		query.setEntity("encodedArtistBean", encodedArtistBean);
		return asSortedSet(query);
	}

	@Override
	public SortedSet<? extends EncodedTrackBean> findTracksWithoutAnAlbum() {
		Query query = getSession().createQuery("from encodedTrackBean where encodedAlbumBean is null");
		return asSortedSet(query);
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
	public EncodedTrackBean createExampleBean() {
		return new EncodedTrackBean();
	}

	@Override
	public void streamTrackData(int id, KnownLengthInputStreamCallback callback)
			throws IOException {
		getStreamer().stream(getSession(), "trackData", "encodedTrackBean", id, callback);
	}
	
	public Streamer getStreamer() {
		return i_streamer;
	}

	public void setStreamer(Streamer streamer) {
		i_streamer = streamer;
	}
}
