package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;

import uk.co.unclealex.flacconverter.encoded.model.EncodedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.util.Partitioner;

public class HibernateEncodedTrackDao extends
		HibernateEncodingDao<EncodedTrackBean> implements EncodedTrackDao {

	private int i_maximumUrlsPerQuery;
	private Partitioner<String> i_partitioner;

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
	public SortedSet<? extends EncodedTrackBean> findByArtistAndEncoderBean(
			EncodedArtistBean encodedArtistBean, EncoderBean encoderBean) {
		Query query = getSession().createQuery(
		"select tr from encodedAlbumBean al join al.encodedTrackBean tr " +
		"where al.encodedArtistBean = :encodedArtistBean and tr.encoderBean = :encoderBean");
		query.setEntity("encodedArtistBean", encodedArtistBean);
		query.setEntity("encoderBean", encoderBean);
		return asSortedSet(query);
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<? extends EncodedTrackBean> asSortedSet(Query query) {
		return new TreeSet<EncodedTrackBean>(query.list());
	}
	
	public EncodedTrackBean findByUrlAndEncoderBean(String url, EncoderBean encoderBean) {
		EncodedTrackBean exampleBean = createExampleBean();
		exampleBean.setFlacUrl(url);
		Criteria criteria = createFindByEncoderBean(exampleBean, encoderBean);
		return (EncodedTrackBean) criteria.uniqueResult();
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

	@SuppressWarnings("unchecked")
	@Override
	public SortedSet<EncodedTrackBean> findByEncoderBean(EncoderBean encoderBean) {
		Criteria criteria = createFindByEncoderBean(createExampleBean(), encoderBean);
		return new TreeSet<EncodedTrackBean>(criteria.list());
	}
	
	@Override
	public EncodedTrackBean createExampleBean() {
		return new EncodedTrackBean();
	}

	public int getMaximumUrlsPerQuery() {
		return i_maximumUrlsPerQuery;
	}

	public void setMaximumUrlsPerQuery(int maximumUrlsPerQuery) {
		i_maximumUrlsPerQuery = maximumUrlsPerQuery;
	}

	public Partitioner<String> getPartitioner() {
		return i_partitioner;
	}

	public void setPartitioner(Partitioner<String> partitioner) {
		i_partitioner = partitioner;
	}
}
