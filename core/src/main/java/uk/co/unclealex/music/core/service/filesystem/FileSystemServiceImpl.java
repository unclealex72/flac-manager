package uk.co.unclealex.music.core.service.filesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.base.service.filesystem.FileSystemService;
import uk.co.unclealex.music.base.service.filesystem.WrongFileTypeException;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.base.visitor.DaoFileVisitor;

@Transactional
public class FileSystemServiceImpl implements FileSystemService {

	private static final Logger log = Logger.getLogger(FileSystemServiceImpl.class);
	
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
	
	protected void addFile(
			OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, Set<FileBean> fileBeans) throws WrongFileTypeException {
		String path = getTitleFormatService().createTitle(encodedTrackBean, ownerBean, true);
		final FileDao fileDao = getFileDao();
		final Date now = new Date();
		ModelReturningFileVisitor fileVisitor = new ModelReturningFileVisitor(path, true, false) {
			@Override
			public FileBean visit(EncodedTrackFileBean encodedTrackFileBean) {
				encodedTrackFileBean.setEncodedTrackBean(encodedTrackBean);
				encodedTrackFileBean.setModificationTimestamp(now);
				fileDao.store(encodedTrackFileBean);
				return null;
			}
		};
		FileBean fileBean = getFileDao().findByPath(path);
		if (fileBean == null) {
			String parentPath = createParentPath(path);
			DirectoryFileBean parent = findOrCreateDirectory(parentPath, fileBeans);
			EncodedTrackFileBean encodedTrackFileBean = new EncodedTrackFileBean();
			encodedTrackFileBean.setCreationTimestamp(now);
			encodedTrackFileBean.setModificationTimestamp(now);
			encodedTrackFileBean.setEncodedTrackBean(encodedTrackBean);
			encodedTrackFileBean.setOwnerBean(ownerBean);
			encodedTrackFileBean.setPath(path);
			encodedTrackFileBean.setParent(parent);
			fileDao.store(encodedTrackFileBean);
			parent.setModificationTimestamp(now);
			fileBeans.add(encodedTrackFileBean);
		}
		else {
			fileBean.accept(fileVisitor);
			if (fileVisitor.getException() != null) {
				throw fileVisitor.getException();
			}
		}
		log.info("Added file " + path);
	}

	protected DirectoryFileBean findOrCreateDirectory(String path, Set<FileBean> fileBeans) throws WrongFileTypeException {
		final List<DirectoryFileBean> wrapper = new ArrayList<DirectoryFileBean>();
		final Date now = new Date();
		ModelReturningFileVisitor fileVisitor = new ModelReturningFileVisitor(path, false, true) {
			@Override
			public FileBean visit(DirectoryFileBean directoryFileBean) {
				wrapper.add(directoryFileBean);
				return directoryFileBean;
			}
		};
		FileDao fileDao = getFileDao();
		FileBean fileBean = fileDao.findByPath(path);
		if (fileBean == null) {
			DirectoryFileBean directoryFileBean = new DirectoryFileBean();
			directoryFileBean.setCreationTimestamp(now);
			directoryFileBean.setModificationTimestamp(now);
			directoryFileBean.setPath(path);
			fileBeans.add(directoryFileBean);
			if ("".equals(path)) {
				fileDao.store(directoryFileBean);
			}
			else {
				String parentPath = createParentPath(path);
				DirectoryFileBean parent = findOrCreateDirectory(parentPath, fileBeans);
				parent.setModificationTimestamp(now);
				directoryFileBean.setParent(parent);
			}
			fileDao.store(directoryFileBean);
			return directoryFileBean;
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
	
	protected void removeFile(FileBean fileBean, Set<String> paths) {
		DirectoryFileBean parent = fileBean.getParent();
		String path = fileBean.getPath();
		paths.add(path);
		FileDao fileDao = getFileDao();
		fileDao.remove(fileBean);
		if (parent != null) {
			Set<FileBean> children = parent.getChildren();
			children.remove(fileBean);
			if (children.isEmpty()) {
				removeFile(parent, paths);
			}
			else {
				parent.setModificationTimestamp(new Date());
				fileDao.store(parent);
			}
		}
		log.info("Removed file " + path);
	}

	protected FileBean findFile(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean) throws WrongFileTypeException {
		String path = getTitleFormatService().createTitle(encodedTrackBean, ownerBean, true);
		return findByPath(path, true, false);
	}
	
	protected FileBean findByPath(String path, boolean allowTrack, boolean allowDirectory) throws WrongFileTypeException {
		FileBean fileBean = getFileDao().findByPath(path);
		if (fileBean == null) {
			return null;
		}
		ModelReturningFileVisitor fileVisitor = new ModelReturningFileVisitor(path, allowTrack, allowDirectory);
		FileBean correctedFileBean = fileBean.accept(fileVisitor);
		if (fileVisitor.getException() != null) {
			throw fileVisitor.getException();
		}
		return correctedFileBean;
	}

	protected class ModelReturningFileVisitor extends DaoFileVisitor<FileBean, WrongFileTypeException> {

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
		public FileBean visit(EncodedTrackFileBean encodedTrackFileBean) {
			if (!isAllowTrack()) {
				return fail(false);
			}
			return encodedTrackFileBean;
		}

		@Override
		public FileBean visit(DirectoryFileBean directoryFileBean) {
			if (!isAllowDirectory()) {
				return fail(true);
			}
			return directoryFileBean;
		}

		public FileBean fail(boolean directoryExpected) {
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