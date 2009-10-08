package uk.co.unclealex.music.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.model.AbstractFileBean;
import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.DataFileBean;
import uk.co.unclealex.music.base.model.DbDirectoryFileBean;
import uk.co.unclealex.music.base.model.DirectoryFileBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.SimpleDataFileBean;
import uk.co.unclealex.music.base.model.SimpleDirectoryFileBean;
import uk.co.unclealex.music.base.visitor.DaoAwareFileVisitor;
import uk.co.unclealex.music.base.visitor.FileVisitor;

public class EncodedTrackFileSystemProvider extends AlbumCoverAwareFileSystemProvider {

	private FileDao i_fileDao;
	private AlbumCoverDao i_albumCoverDao;
	private AlbumCoverService i_albumCoverService;
	
	@Override
	public int countChildren(DirectoryFileBean directoryFileBean) {
		return getFileDao().countChildren(directoryFileBean);
	}

	@Override
	public int countFiles() {
		int fileCount = getFileDao().countFiles();
		int selectedAlbumsCount = getAlbumCoverDao().countSelectedAlbums();
		return fileCount + selectedAlbumsCount;
	}

	@Override
	protected AlbumCoverBean findAlbumCoverByPath(String directory, final String path) throws FileNotFoundException {
		DaoAwareFileVisitor<AlbumCoverBean, Exception> fileVisitor = new DaoAwareFileVisitor<AlbumCoverBean, Exception>() {
			@Override
			public AlbumCoverBean visit(EncodedTrackFileBean encodedTrackFileBean) {
				return null;
			}
			@Override
			public AlbumCoverBean visit(DbDirectoryFileBean dbDirectoryFileBean) {
				return findAlbumCoverByPath(dbDirectoryFileBean, path);
			}
		};
		AbstractFileBean fileBean = getFileDao().findByPath(directory);
		return fileBean.accept(fileVisitor);
	}
	
	protected AlbumCoverBean findAlbumCoverByPath(DbDirectoryFileBean directoryFileBean, String path) {
		AlbumCoverBean albumCoverBean = null;
		DaoAwareFileVisitor<AlbumCoverBean, Exception> fileVisitor = new DaoAwareFileVisitor<AlbumCoverBean, Exception>() {
			@Override
			public AlbumCoverBean visit(EncodedTrackFileBean encodedTrackFileBean) {
				EncodedTrackBean encodedTrackBean = encodedTrackFileBean.getEncodedTrackBean();
				return getAlbumCoverService().findSelectedCoverForEncodedTrack(encodedTrackBean);
			}
			@Override
			public AlbumCoverBean visit(DbDirectoryFileBean directoryFileBean) {
				return null;
			}
		};
		Set<AbstractFileBean> children = directoryFileBean.getChildren();
		for (Iterator<AbstractFileBean> iter = children.iterator(); albumCoverBean == null && iter.hasNext(); ) {
			albumCoverBean = iter.next().accept(fileVisitor);
		}
		return albumCoverBean;
	}

	@Override
	protected FileBean findNonAlbumCoverByPath(final String path) throws FileNotFoundException {
		FileBean fileBean = getFileDao().findByPath(path);
		if (fileBean == null) {
			throw new FileNotFoundException(path);
		}
		FileVisitor<FileBean, Exception> fileVisitor = new FileVisitor<FileBean, Exception>() {
			@Override
			public FileBean visit(DataFileBean dataFileBean) {
				return dataFileBean;
			}
			
			@Override
			public FileBean visit(DirectoryFileBean directoryFileBean) {
				AlbumCoverBean albumCoverBean = findAlbumCoverByPath(directoryFileBean, path);
				if (albumCoverBean == null) {
					return directoryFileBean;
				}
				else {
					Set<FileBean> newChildren = new HashSet<FileBean>(directoryFileBean.getChildren());
					newChildren.add(new SimpleDataFileBean(path, albumCoverBean.getCoverDataBean()));
					return new SimpleDirectoryFileBean(path, newChildren);
				}
			}
		};
		return fileBean.accept(fileVisitor);
	}

	@Override
	public long getTotalSize() {
		Set<String> allPaths = new HashSet<String>(getFileDao().findAllPaths());
		allPaths.addAll(getAlbumCoverDao().findSelectedAlbumCoverFilePaths());
		long size = 0;
		for (String path : allPaths) {
			size += new File(path).length();
		}
		return size;
	}

	public FileDao getFileDao() {
		return i_fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		i_fileDao = fileDao;
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}

}
