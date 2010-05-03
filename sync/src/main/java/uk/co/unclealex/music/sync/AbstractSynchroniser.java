// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AbstractSynchroniser.java

package uk.co.unclealex.music.sync;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import uk.co.unclealex.music.Device;
import uk.co.unclealex.music.DeviceService;
import uk.co.unclealex.music.FileService;


public abstract class AbstractSynchroniser<D extends Device> implements Synchroniser {

	protected Logger log = Logger.getLogger(getClass());
	
	private D i_device;
	private DeviceService i_deviceService;
	private FileService i_fileService;
	
	public void postConstruct(D device) {
		setDevice(device);
	}

	@Override
	public void synchronise() throws IOException {
		try {
			initialiseDevice();
			Set<DeviceFile> deviceFiles = listDeviceFiles();
			SortedSet<LocalFile> localFiles = listLocalFiles();
			Map<String, DeviceFile> deviceFilesByRelativePath = mapByRelativePath(deviceFiles);
			Map<String, LocalFile> localFilesByRelativePath = mapByRelativePath(localFiles);
			SortedSet<DeviceFile> deviceFilesToRemove = new TreeSet<DeviceFile>();
			SortedSet<LocalFile> localFilesToAdd = new TreeSet<LocalFile>();
			for (Map.Entry<String, LocalFile> entry : localFilesByRelativePath.entrySet()) {
				String relativePath = entry.getKey();
				LocalFile localFile = entry.getValue();
				DeviceFile deviceFile = deviceFilesByRelativePath.get(relativePath);
				if (deviceFile == null || localFile.getLastModified() > deviceFile.getLastModified()) {
					if (deviceFile != null) {
						deviceFilesToRemove.add(deviceFile);
						deviceFilesByRelativePath.remove(relativePath);
					}
					localFilesToAdd.add(localFile);
				}
				else if (deviceFile != null) {
					log.info(String.format("File %s will be kept.", deviceFile));
				}
			}
			deviceFilesToRemove.addAll(deviceFilesByRelativePath.values());
			for (DeviceFile deviceFile : deviceFilesToRemove) {
				log.info("Removing " + deviceFile.getRelativePath());
				remove(deviceFile);
			}
			for (LocalFile localFile : localFilesToAdd) {
				log.info("Adding " + localFile.getRelativePath());
				add(localFile);
			}
		}
		finally {
			closeDevice();
		}
	}
	
	protected SortedSet<LocalFile> listLocalFiles() throws IOException {
		RelativePathFileFactory<LocalFile> factory = new RelativePathFileFactory<LocalFile>() {
			@Override
			public LocalFile createRelativeFilePath(String relativePath, File f) throws IOException {
				return new LocalFile(f.getCanonicalFile(), relativePath);
			}
		};
		return listRelativePathFiles(getDeviceService().getDeviceDirectory(getDevice()), factory);
	}
	
	protected <R extends RelativePathFile<R>> SortedSet<R> listRelativePathFiles(
			File directory, RelativePathFileFactory<R> factory) throws IOException {
		SortedSet<R> relativePathFiles = new TreeSet<R>();
		Device device = getDevice();
		String extension = device.getEncoding().getExtension();
		File deviceDirectory = getDeviceService().getDeviceDirectory(device);
		for (File file : deviceDirectory.listFiles()) {
			listRelativePathFiles(relativePathFiles, file, "", extension, factory);
		}
		return relativePathFiles;
	}

	protected <R extends RelativePathFile<R>> void listRelativePathFiles(
			SortedSet<R> relativePathFiles, File f, String path, String extension, RelativePathFileFactory<R> factory) throws IOException {
		String name = f.getName();
		if (f.isDirectory()) {
			path += name + File.separatorChar;
			for (File child : f.listFiles()) {
				listRelativePathFiles(relativePathFiles, child, path, extension, factory);
			}
		}
		else if (extension.equals(FilenameUtils.getExtension(name))) {
			relativePathFiles.add(factory.createRelativeFilePath(path + name, f));
		}
	}

	protected interface RelativePathFileFactory<R extends RelativePathFile<R>> {
		public R createRelativeFilePath(String relativePath, File f) throws IOException;
	}
	
	protected <R extends RelativePathFile<R>> Map<String, R> mapByRelativePath(Set<R> relativePathFiles) {
		Map<String, R> filesByRelativePath = new TreeMap<String, R>();
		for (R file : relativePathFiles) {
			filesByRelativePath.put(file.getRelativePath(), file);
		}
		return filesByRelativePath;
	}

	protected abstract void initialiseDevice() throws IOException;

	protected abstract Set<DeviceFile> listDeviceFiles() throws IOException;

	protected abstract void add(LocalFile localFile) throws IOException;

	protected abstract void remove(DeviceFile deviceFile) throws IOException;

	protected abstract void closeDevice() throws IOException;

	public D getDevice() {
		return i_device;
	}

	public void setDevice(D device) {
		i_device = device;
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public FileService getFileService() {
		return i_fileService;
	}

	public void setFileService(FileService fileService) {
		i_fileService = fileService;
	}

}
