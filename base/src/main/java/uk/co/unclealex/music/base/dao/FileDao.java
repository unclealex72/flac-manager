package uk.co.unclealex.music.base.dao;

import java.util.Set;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.AbstractFileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;

public interface FileDao extends KeyedDao<AbstractFileBean> {

	public AbstractFileBean findByPath(String path);

	public int countChildren(DirectoryFileBean directoryFileBean);

	public Set<String> findAllPaths();

	public int countFiles();
}
