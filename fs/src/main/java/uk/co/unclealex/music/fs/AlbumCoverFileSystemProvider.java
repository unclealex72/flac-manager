package uk.co.unclealex.music.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.log4j.Logger;

import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.visitor.FileVisitor;
import uk.co.unclealex.music.core.service.SlimServerService;

public class AlbumCoverFileSystemProvider extends AlbumCoverAwareFileSystemProvider implements FileSystemProvider {

	private static Logger log = Logger.getLogger(AlbumCoverFileSystemProvider.class);
	
	private AlbumCoverDao i_albumCoverDao;
	private SlimServerService i_slimServerService;
	private FlacTrackDao i_flacTrackDao;
	
	protected FileBean findNonAlbumCoverByPath(final String path) throws IOException {
		String underlyingPath = getSlimServerService().makePathAbsolute(path);
		File underlyingFile = new File(underlyingPath);
		if (underlyingFile.exists() && underlyingFile.isDirectory()) {
			return null;
		}
		return new DirectoryWrappingFileBean(underlyingFile);
	}

	@SuppressWarnings("unchecked")
	protected AlbumCoverBean findAlbumCoverByPath(String directory, String path) throws IOException {
		File directoryFile = new File(getSlimServerService().makePathAbsolute(directory));
		if (!directoryFile.exists()) {
			throw createFileNotFoundException(path);
		}
		Collection<File> childFiles = FileUtils.listFiles(directoryFile, FileFileFilter.FILE, null);
		Iterator<File> iterator = childFiles.iterator();
		if (iterator.hasNext()) {
			File childFile = iterator.next();
			return findAlbumCoverByFlacTrackFile(childFile);
		}
		else {
			return null;
		}
	}

	protected AlbumCoverBean findAlbumCoverByFlacTrackFile(File childFile) throws IOException,
			FileNotFoundException {
		FlacTrackBean flacTrackBean = getFlacTrackDao().findByFile(childFile);
		if (flacTrackBean == null) {
			return null;
		}
		FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
		return getAlbumCoverDao().findSelectedCoverForAlbum(flacAlbumBean.getFlacArtistBean().getCode(), flacAlbumBean.getCode());
	}

	@Override
	public int countChildren(DirectoryFileBean directoryFileBean) {
		return directoryFileBean.getChildren().size();
	}

	@Override
	public int countFiles() {
		return getAlbumCoverDao().countSelectedAlbums();
	}

	@Override
	public long getTotalSize() {
		Set<String> albumCoverFilePaths = getAlbumCoverDao().findSelectedAlbumCoverFilePaths();
		long totalSize = 0;
		for (String albumCoverFilePath : albumCoverFilePaths) {
			totalSize += new File(albumCoverFilePath).length();
		}
		return totalSize;
	}

	public class DirectoryWrappingFileBean implements DirectoryFileBean {
		
		private File i_directory;
		private Set<FileBean> i_children;
		
		public DirectoryWrappingFileBean(File directory) {
			super();
			i_directory = directory;
		}

		@Override
		public Integer getId() {
			return getDirectory().hashCode();
		}
		
		protected Set<FileBean> createChildren() {
			Set<FileBean> children = new HashSet<FileBean>();
			File[] childFiles = getDirectory().listFiles();
			boolean trackFound = false;
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					children.add(new DirectoryWrappingFileBean(childFile));
				}
				else if (!trackFound) {
					try {
						AlbumCoverBean albumCoverBean = findAlbumCoverByFlacTrackFile(childFile);
						if (albumCoverBean != null) {
							trackFound = true;
							children.add(
									createAlbumCoverFileBean(getSlimServerService().makePathRelative(childFile.getPath()), albumCoverBean));
						}
					}
					catch (IOException e) {
						log.warn("Could not find an album cover for file " + childFile, e);
					}
				}
			}
			return children;
		}

		@Override
		public String getPath() {
			return getSlimServerService().makePathRelative(getDirectory().getPath());
		}
		
		@Override
		public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor) {
			return fileVisitor.visit(this);
		}
		
		@Override
		public Set<FileBean> getChildren() {
			if (i_children == null) {
				i_children = createChildren();
			}
			return i_children;
		}
		
		public File getDirectory() {
			return i_directory;
		}
	}
	
	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

	public SlimServerService getSlimServerService() {
		return i_slimServerService;
	}

	public void setSlimServerService(SlimServerService slimServerService) {
		i_slimServerService = slimServerService;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
