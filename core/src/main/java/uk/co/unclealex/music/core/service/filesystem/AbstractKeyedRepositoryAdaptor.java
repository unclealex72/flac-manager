package uk.co.unclealex.music.core.service.filesystem;

import java.util.Set;

import uk.co.unclealex.music.base.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.model.KeyedBean;
import uk.co.unclealex.music.base.service.filesystem.RepositoryAdaptor;

public abstract class AbstractKeyedRepositoryAdaptor<T extends KeyedBean<T>> implements RepositoryAdaptor<T> {

	public abstract KeyedReadOnlyDao<T> getDao();
	
	@Override
	public Set<T> getAllElements() {
		return getDao().getAll();
	}

	@Override
	public T findById(int id) {
		return getDao().findById(id);
	}
	

}
