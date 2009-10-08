package uk.co.unclealex.music.core.dao;

import java.util.Set;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.model.AbstractFileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.visitor.FileVisitor;

@Transactional
public class HibernateFileDao extends HibernateKeyedDao<AbstractFileBean> implements FileDao {

	@Override
	public AbstractFileBean createExampleBean() {
		return new AbstractFileBean() {
			@Override
			public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor) {
				return fileVisitor.visit(this);
			}
		};
	}

	@Override
	public int countChildren(DirectoryFileBean directoryFileBean) {
		return directoryFileBean.getChildren().size();
	}

	@Override
	public int countFiles() {
		return uniqueResult(getSession().createQuery("select count(fileBean) from fileBean"), Integer.class);
	}

	@Override
	public Set<String> findAllPaths() {
		return asSortedSet(getSession().createQuery("select path from fileBean"), String.class);
	}

	@Override
	public AbstractFileBean findByPath(String path) {
		Query query = getSession().createQuery("from fileBean where path = :path").setString("path", path);
		return uniqueResult(query, AbstractFileBean.class);
	}

}
