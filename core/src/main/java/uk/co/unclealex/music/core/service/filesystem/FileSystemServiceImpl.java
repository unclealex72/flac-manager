package uk.co.unclealex.music.core.service.filesystem;

import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

@Service
@Transactional
public class FileSystemServiceImpl implements FileSystemService {

	private Collection<String> i_fileSystemTitleFormats;
	private FileSystemCache i_fileSystemCache;
	private Object i_cacheLock = new Object();
	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	@PostConstruct
	public void rebuildCache() {
		new Callback<Object>() {
			@Override
			public void doInCache(FileSystemCache fileSystemCache) {
				fileSystemCache.createCache(getFileSystemTitleFormats());
			}
		}.execute();
	}

	@Override
	public EncodedTrackBean findByPath(String path) throws PathNotFoundException {
		if (path.endsWith("/")) {
			throw new PathNotFoundException(path);
		}
		ValueReturningPathInformationBeanVisitor<Integer> visitor = 
			new ValueReturningPathInformationBeanVisitor<Integer>() {
			@Override
			public void visit(DirectoryInformationBean directoryInformationBean) {
				setValue(null);
			}
			@Override
			public void visit(FileInformationBean fileInformationBean) {
				setValue(fileInformationBean.getEncodedTrackBeanId());
			}
		};
		Integer id = visitAndReturnInCallback(path, visitor);
		if (id == null) {
			throw new PathNotFoundException(path);
		}
		return getEncodedTrackDao().findById(id);
	}

	@Override
	public SortedSet<String> getChildren(String directory)
			throws PathNotFoundException {
		ValueReturningPathInformationBeanVisitor<SortedSet<String>> visitor =
			new ValueReturningPathInformationBeanVisitor<SortedSet<String>>() {
			@Override
			public void visit(DirectoryInformationBean directoryInformationBean) {
				setValue(directoryInformationBean.getChildren());
			}
			@Override
			public void visit(FileInformationBean fileInformationBean) {
				setValue(new TreeSet<String>());
			}
		};
		return visitAndReturnInCallback(directory, visitor);
	}

	@Override
	public Long getLength(String path) throws PathNotFoundException {
		return getPathInformationBean(path).getLength();
	}

	@Override
	public Date getModificationDate(String path) throws PathNotFoundException {
		return getPathInformationBean(path).getLastModifiedDate();
	}

	@Override
	public boolean isDirectory(String path) throws PathNotFoundException {
		return visitAndReturnInCallback(path, new IsDirectoryPathInformationBeanVisitor());
	}

	@Override
	public boolean objectExists(String path) {
		try {
			return new Callback<Boolean>() {
				@Override
				public Boolean doInCache(FileSystemCache fileSystemCache, String path) {
					return fileSystemCache.findPath(path) != null;
				}
			}.execute(path);
		}
		catch (PathNotFoundException e) {
			throw new IllegalStateException("A path not found exception should not occur here.", e);
		}
	}

	protected PathInformationBean getPathInformationBean(String path) throws PathNotFoundException {
		return new Callback<PathInformationBean>() {
			@Override
			public PathInformationBean doInCache(FileSystemCache fileSystemCache,
					String path) throws PathNotFoundException {
				PathInformationBean pathInformationBean = fileSystemCache.findPath(path);
				if (pathInformationBean == null) {
					throw new PathNotFoundException(path);
				}
				return pathInformationBean;
			}
		}.execute(path);
	}
	
	protected void visitInCallback(String path, final PathInformationBeanVisitor visitor) throws PathNotFoundException {
		new Callback<Object>() {
		@Override
			public Object doInCache(FileSystemCache fileSystemCache, String path) throws PathNotFoundException {
				PathInformationBean pathInformationBean = fileSystemCache.findPath(path);
				pathInformationBean.accept(visitor);
				return null;
			}	
		}.execute(path);
	}
	
	protected <E> E visitAndReturnInCallback(
			String path, final ValueReturningPathInformationBeanVisitor<E> visitor) throws PathNotFoundException {
		return new Callback<E>() {
		@Override
			public E doInCache(FileSystemCache fileSystemCache, String path) throws PathNotFoundException {
				PathInformationBean pathInformationBean = fileSystemCache.findPath(path);
				if (pathInformationBean == null) {
					throw new PathNotFoundException(path);
				}
				pathInformationBean.accept(visitor);
				return visitor.getValue();
			}	
		}.execute(path);
	}

	protected abstract class Callback<E> {
		
		public E doInCache(FileSystemCache fileSystemCache, String path) throws PathNotFoundException {
			return null;
		}
		
		public void doInCache(FileSystemCache fileSystemCache) {
			// Default is to do nothing
		}
		
		public E execute(String path) throws PathNotFoundException {
			path = StringUtils.removeStart(
					StringUtils.trimToEmpty(FilenameUtils.normalizeNoEndSeparator(path)), "/");
			synchronized (getCacheLock()) {
				return doInCache(getFileSystemCache(), path);
			}
		}

		public void execute() {
			synchronized (getCacheLock()) {
				doInCache(getFileSystemCache());
			}
		}
	}

	public Collection<String> getFileSystemTitleFormats() {
		return i_fileSystemTitleFormats;
	}

	@Required
	public void setFileSystemTitleFormats(Collection<String> fileSystemTitleFormats) {
		i_fileSystemTitleFormats = fileSystemTitleFormats;
	}

	public FileSystemCache getFileSystemCache() {
		return i_fileSystemCache;
	}

	@Required
	public void setFileSystemCache(FileSystemCache fileSystemCache) {
		i_fileSystemCache = fileSystemCache;
	}

	public Object getCacheLock() {
		return i_cacheLock;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}