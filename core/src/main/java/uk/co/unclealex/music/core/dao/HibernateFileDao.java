package uk.co.unclealex.music.core.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.visitor.DaoFileVisitor;

@Transactional
public class HibernateFileDao extends HibernateKeyedDao<FileBean> implements FileDao {

	@Override
	public FileBean createExampleBean() {
		return new FileBean() {
			@Override
			public <R, E extends Exception> R accept(DaoFileVisitor<R, E> fileVisitor) {
				return fileVisitor.visit(this);
			}
		};
	}

	@Override
	public Set<EncodedTrackFileBean> getAllNormalFiles() {
		return asSortedSet(getSession().createQuery("from encodedTrackFileBean"), EncodedTrackFileBean.class);
	}
	
	@Override
	public Map<String, Integer> findAllRealFilenames() {
		Query query = getSession().createQuery(
				"select t.filename, count(t.filename) from " +
				"encodedTrackFileBean etfb inner join etfb.encodedTrackBean etb inner join etb.trackDataBean t group by t.filename");
		List<Object[]> results = asList(query, Object[].class);
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (Object[] result : results) {
			resultMap.put((String) result[0], ((Long) result[1]).intValue());
		}
		return resultMap;
	}
	
	@Override
	public int countChildren(DirectoryFileBean directoryFileBean) {
		Query query = getSession().createQuery("select count(fb) from fileBean fb where fb.parent = :directoryFileBean");
		query.setEntity("directoryFileBean", directoryFileBean);
		return uniqueResult(query, Long.class).intValue();
	}

	@Override
	public Set<FileBean> getChildren(DirectoryFileBean directoryFileBean) {
		Query query = getSession().createQuery("from fileBean where parent = :parent");
		query.setEntity("parent", directoryFileBean);
		return asSortedSet(query);
	}
	
	@Override
	public int countFiles() {
		return uniqueResult(getSession().createQuery("select count(fileBean) from fileBean"), Long.class).intValue();
	}

	@Override
	public Set<String> findAllPaths() {
		return asSortedSet(getSession().createQuery("select path from fileBean"), String.class);
	}

	@Override
	public FileBean findByPath(String path) {
		Query query = getSession().createQuery("from fileBean where path = :path").setString("path", path);
		return uniqueResult(query, FileBean.class);
	}

}
