package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Expression;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.util.Partitioner;

public class HibernateEncodedTrackDao extends
		HibernateEncodingDao<EncodedTrackBean> implements EncodedTrackDao {

	private int i_maximumUrlsPerQuery;
	private Partitioner<String> i_partitioner;
	
	@SuppressWarnings("unchecked")
	public SortedSet<EncodedTrackBean> findByUrlsAndEncoderBean(Collection<String> urls, EncoderBean encoderBean) {
		SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
		for (Collection<String> urlPartition : getPartitioner().partition(urls, getMaximumUrlsPerQuery())) {
			EncodedTrackBean exampleBean = createExampleBean();
			Criteria criteria = 
				createFindByEncoderBean(exampleBean, encoderBean, Expression.in("flacUrl", urlPartition));
			encodedTrackBeans.addAll(criteria.list());
		}
		return encodedTrackBeans;
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
