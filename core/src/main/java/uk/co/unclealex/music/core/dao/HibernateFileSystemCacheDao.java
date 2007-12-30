package uk.co.unclealex.music.core.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.FileSystemCacheBean;

@Repository
@Transactional
public class HibernateFileSystemCacheDao extends HibernateDaoSupport implements FileSystemCacheDao {

	@Autowired
	public HibernateFileSystemCacheDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super();
		setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	protected FileSystemCacheBean retrieveSingletonBean() {
		Criteria crit = getSession().createCriteria(FileSystemCacheBean.class);
		List<FileSystemCacheBean> all = crit.list();
		int size = all.size();
		if (size > 1) {
			throw new IllegalStateException(
				"Found " + size + " file system cache beans when there should be at most one. " +
				"Please delete them all manually.");
		}
		return size==0?new FileSystemCacheBean():all.get(0);
	}
	
	@Override
	public boolean isRebuildRequired() {
		return retrieveSingletonBean().isRebuildRequired();
	}

	@Override
	public void setRebuildRequired(boolean rebuildRequired) {
		FileSystemCacheBean fileSystemCacheBean = retrieveSingletonBean();
		fileSystemCacheBean.setRebuildRequired(rebuildRequired);
		getSession().saveOrUpdate(fileSystemCacheBean);
	}

}
