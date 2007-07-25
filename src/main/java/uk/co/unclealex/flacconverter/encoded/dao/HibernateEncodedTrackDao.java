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

public class HibernateEncodedTrackDao extends
		HibernateEncodingDao<EncodedTrackBean> implements EncodedTrackDao {

	@Override
	public void store(EncodedTrackBean encodedTrackBean) {
		getSession().saveOrUpdate(encodedTrackBean.getTrackDataBean());
		super.store(encodedTrackBean);
	}
	
	@SuppressWarnings("unchecked")
	public SortedSet<EncodedTrackBean> findByUrlsAndEncoderBean(Collection<String> urls, EncoderBean encoderBean) {
		EncodedTrackBean exampleBean = createExampleBean();
		Criteria criteria = createFindByEncoderBean(exampleBean, encoderBean, Expression.in("flacUrl", urls));
		return new TreeSet<EncodedTrackBean>(criteria.list());
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
}
