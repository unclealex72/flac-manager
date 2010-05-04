package uk.co.unclealex.music.sync;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.music.FileSystemDevice;

public class FileSystemSynchroniser extends AbstractFileSystemSynchroniser<FileSystemDevice> {

	@Override
	protected void initialiseDevice() throws IOException {
		setDeviceRoot(getDevice().getMountPoint());
	}

	@Override
	protected DeviceFile createDeviceFile(String relativePath, File f) {
		return new DeviceFile(relativePath, relativePath, f.lastModified());
	}

	@Override
	protected String createRemoteRelativeFilePath(LocalFile localFile) {
		return localFile.getRelativePath();
	}

	@Override
	protected void disconnect() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
