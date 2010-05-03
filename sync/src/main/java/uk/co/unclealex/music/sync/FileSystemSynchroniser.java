package uk.co.unclealex.music.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import uk.co.unclealex.music.FileSystemDevice;

public class FileSystemSynchroniser extends AbstractSynchroniser<FileSystemDevice> {

	private File i_mountPoint;
	
	@Override
	protected void initialiseDevice() throws IOException {
		// Do nothing
	}

	@Override
	protected Set<DeviceFile> listDeviceFiles() throws IOException {
		RelativePathFileFactory<DeviceFile> factory = new RelativePathFileFactory<DeviceFile>() {
			@Override
			public DeviceFile createRelativeFilePath(String relativePath, File f) throws IOException {
				return new DeviceFile(relativePath, relativePath, f.lastModified());
			}
		};
		return listRelativePathFiles(getMountPoint(), factory);
	}

	@Override
	protected void remove(DeviceFile deviceFile) throws IOException {
		File file = new File(getMountPoint(), deviceFile.getRelativePath());
		file.delete();
	}

	@Override
	protected void add(LocalFile localFile) throws IOException {
		File targetFile = new File(getMountPoint(), localFile.getRelativePath());
		targetFile.getParentFile().mkdirs();
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			File sourceFile = localFile.getFile();
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(targetFile);
			in.getChannel().transferTo(0, sourceFile.length(), out.getChannel());
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	protected void closeDevice() throws IOException {
		removeEmptyDirectories(getMountPoint(), false, getDevice().getEncoding().getExtension());
	}

	protected void removeEmptyDirectories(File dir, boolean deleteDirectory, String extension) {
		for (File child : dir.listFiles()) {
			if (child.isDirectory()) {
				removeEmptyDirectories(dir, true, extension);
			}
			else if (!extension.equals(FilenameUtils.getExtension(child.getName()))) {
				child.delete();
			}
		}
		if (dir.listFiles().length == 0) {
			dir.delete();
		}
	}

	public File getMountPoint() {
		return i_mountPoint;
	}

	public void setMountPoint(File mountPoint) {
		i_mountPoint = mountPoint;
	}

}
