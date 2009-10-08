package uk.co.unclealex.music.core.dao;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedReadOnlyDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.FlacTrackBean;

@Transactional
public class HibernateFlacTrackDao extends HibernateKeyedReadOnlyDao<FlacTrackBean> implements FlacTrackDao {

	@Override
	public SortedSet<String> getAllUrls() {
		return asSortedSet(getSession().createQuery("select url from FlacTrackBean order by url"), String.class);
	}
	
	@Override
	public FlacTrackBean findByFile(File flacFile) throws IOException {
		return findByUrl("file://" + flacFile.getCanonicalPath());
	}
	
	public FlacTrackBean findByUrl(String url) {
		FlacTrackBean example = createExampleBean();
		example.setUrl(url);
		return (FlacTrackBean) createCriteria(example).uniqueResult();
	}
	
	@Override
	public int countTracks() {
		Criteria criteria = createCriteria(createExampleBean()).setProjection(Projections.count("url"));
		return (Integer) criteria.uniqueResult();
	}
	
	@Override
	public SortedSet<FlacTrackBean> findTracksStartingWith(String url) {
		Query query = getSession().createQuery("from FlacTrackBean where url like :url and type = 'flc'");
		query.setString("url", url + "%");
		return asSortedSet(query);
	}
	
	@Override
	public FlacTrackBean createExampleBean() {
		FlacTrackBean example = new FlacTrackBean();
		example.setType("flc");
		return example;
	}	
}
