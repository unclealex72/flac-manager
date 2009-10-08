package uk.co.unclealex.music.core.service.filesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.AbstractFileBean;
import uk.co.unclealex.music.base.model.DbDirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.base.service.filesystem.FileSystemService;
import uk.co.unclealex.music.base.service.filesystem.WrongFileTypeException;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.base.visitor.DaoAwareFileVisitor;

@Transactional
public class FileSystemServiceImpl implements FileSystemService {

	private TitleFormatService i_titleFormatService;
	private OwnerService i_ownerService;
	private OwnerDao i_ownerDao;
	
	private FileDao i_fileDao;
	
	@Override
	public Set<FileBean> addFiles(EncodedTrackBean encodedTrackBean) throws WrongFileTypeException {
		return addFiles(encodedTrackBean.getOwnerBeans(), encodedTrackBean);
	}

	@Override
	public Set<String> removeFiles(EncodedTrackBean encodedTrackBean) throws WrongFileTypeException {
		return removeFiles(encodedTrackBean.getOwnerBeans(), encodedTrackBean);
	}
	
	@Override
	public Set<FileBean> addFiles(Collection<OwnerBean> ownerBeans, EncodedTrackBean encodedTrackBean) throws WrongFileTypeException {
		Set<FileBean> fileBeans = new LinkedHashSet<FileBean>();
		for (OwnerBean ownerBean : ownerBeans) {
			addFile(ownerBean, encodedTrackBean, fileBeans);
		}
		return fileBeans;
	}
	
	protected void addFile(OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, Set<FileBean> fileBeans) throws WrongFileTypeException {
		String path = getTitleFormatService().createTitle(encodedTrackBean, ownerBean);
		ModelReturningFileVisitor fileVisitor = new ModelReturningFileVisitor(path, true, false) {
			@Override
			public AbstractFileBean visit(EncodedTrackFileBean encodedTrackFileBean) {
				encodedTrackFileBean.setEncodedTrackBean(encodedTrackBean);
				getFileDao().store(encodedTrackFileBean);
				return null;
			}
		};
		AbstractFileBean fileBean = getFileDao().findByPath(path);
		if (fileBean == null) {
			String parentPath = createParentPath(path);
			DbDirectoryFileBean parent = findOrCreateDirectory(parentPath, fileBeans);
			EncodedTrackFileBean encodedTrackFileBean = new EncodedTrackFileBean();
			encodedTrackFileBean.setEncodedTrackBean(encodedTrackBean);
			encodedTrackFileBean.setParent(parent);
			encodedTrackFileBean.setPath(path);
			getFileDao().store(encodedTrackFileBean);
			fileBeans.add(encodedTrackFileBean);
		}
		else {
			fileBean.accept(fileVisitor);
			if (fileVisitor.getException() != null) {
				throw fileVisitor.getException();
			}
		}
	}

	protected DbDirectoryFileBean findOrCreateDirectory(String path, Set<FileBean> fileBeans) throws WrongFileTypeException {
		final List<DbDirectoryFileBean> wrapper = new ArrayList<DbDirectoryFileBean>();
		ModelReturningFileVisitor fileVisitor = new ModelReturningFileVisitor(path, false, true) {
			@Override
			public AbstractFileBean visit(DbDirectoryFileBean dbDirectoryFileBean) {
				wrapper.add(dbDirectoryFileBean);
				return dbDirectoryFileBean;
			}
		};
		AbstractFileBean fileBean = getFileDao().findByPath(path);
		if (fileBean == null) {
			DbDirectoryFileBean dbDirectoryFileBean = new DbDirectoryFileBean();
			dbDirectoryFileBean.setPath(path);
			fileBeans.add(dbDirectoryFileBean);
			if ("".equals(path)) {
				getFileDao().store(dbDirectoryFileBean);
			}
			else {
				String parentPath = createParentPath(path);
				DbDirectoryFileBean parent = findOrCreateDirectory(parentPath, fileBeans);
				dbDirectoryFileBean.setParent(parent);
				parent.getChildren().add(dbDirectoryFileBean);
				getFileDao().store(parent);
			}
			return dbDirectoryFileBean;
		}
		else {
			fileBean.accept(fileVisitor);
			if (fileVisitor.getException() != null) {
				throw fileVisitor.getException();
			}
			return wrapper.get(0);
		}
	}

	protected String createParentPath(String path) {
		return FilenameUtils.getFullPathNoEndSeparator(path);
	}
	
	@Override
	public Set<String> removeFiles(Collection<OwnerBean> ownerBeans, EncodedTrackBean encodedTrackBean) throws WrongFileTypeException {
		Set<String> paths = new LinkedHashSet<String>();
		for (OwnerBean ownerBean : ownerBeans) {
			removeFile(findFile(ownerBean, encodedTrackBean), paths);
		}
		return paths;
	}
	
	protected void removeFile(AbstractFileBean abstractFileBean, Set<String> paths) {
		DbDirectoryFileBean parent = abstractFileBean.getParent();
		paths.add(abstractFileBean.getPath());
		FileDao fileDao = getFileDao();
		fileDao.remove(abstractFileBean);
		// Don't delete root!
		if (parent != null && parent.getChildren().isEmpty()) {
			removeFile(parent, paths);
		}
	}

	protected AbstractFileBean findFile(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean) throws WrongFileTypeException {
		String path = getTitleFormatService().createTitle(encodedTrackBean, ownerBean);
		return findByPath(path, true, false);
	}
	
	protected AbstractFileBean findByPath(String path, boolean allowTrack, boolean allowDirectory) throws WrongFileTypeException {
		AbstractFileBean fileBean = getFileDao().findByPath(path);
		if (fileBean == null) {
			return null;
		}
		ModelReturningFileVisitor fileVisitor = new ModelReturningFileVisitor(path, allowTrack, allowDirectory);
		AbstractFileBean abstractFileBean = fileBean.accept(fileVisitor);
		if (fileVisitor.getException() != null) {
			throw fileVisitor.getException();
		}
		return abstractFileBean;
	}

	protected class ModelReturningFileVisitor extends DaoAwareFileVisitor<AbstractFileBean, WrongFileTypeException> {

		private boolean i_allowTrack;
		private boolean i_allowDirectory;
		private String i_path;
		
		public ModelReturningFileVisitor(String path, boolean allowTrack, boolean allowDirectory) {
			super();
			i_allowTrack = allowTrack;
			i_allowDirectory = allowDirectory;
			i_path = path;
		}

		@Override
		public AbstractFileBean visit(EncodedTrackFileBean encodedTrackFileBean) {
			if (!isAllowTrack()) {
				return fail(false);
			}
			return encodedTrackFileBean;
		}

		@Override
		public AbstractFileBean visit(DbDirectoryFileBean dbDirectoryFileBean) {
			if (!isAllowDirectory()) {
				return fail(true);
			}
			return dbDirectoryFileBean;
		}

		public AbstractFileBean fail(boolean directoryExpected) {
			setException(new WrongFileTypeException(getPath(), directoryExpected));
			return null;
		}

		public String getPath() {
			return i_path;
		}
		
		public boolean isAllowTrack() {
			return i_allowTrack;
		}

		public boolean isAllowDirectory() {
			return i_allowDirectory;
		}
		
	}
	
	public TitleFormatService getTitleFormatService() {
		return i_titleFormatService;
	}

	public void setTitleFormatService(TitleFormatService titleFormatService) {
		i_titleFormatService = titleFormatService;
	}

	public FileDao getFileDao() {
		return i_fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		i_fileDao = fileDao;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}
	
	
}