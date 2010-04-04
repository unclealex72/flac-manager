package uk.co.unclealex.music.base.dao;

import java.util.Map;
import java.util.Set;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;

public interface FileDao extends KeyedDao<FileBean> {

	public FileBean findByPath(String path);

	public int countChildren(DirectoryFileBean directoryFileBean);

	public Set<String> findAllPaths();

	public int countFiles();

	public Map<String, Integer> findAllRealFilenames();

	public Set<FileBean> getChildren(DirectoryFileBean directoryFileBean);

	public Set<EncodedTrackFileBean> getAllNormalFiles();
}
