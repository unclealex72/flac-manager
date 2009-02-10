package uk.co.unclealex.music.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.process.service.ProcessResult;
import uk.co.unclealex.music.base.process.service.ProcessService;
import uk.co.unclealex.music.base.service.DeviceService;

@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {

	private static final Logger log = Logger.getLogger(DeviceServiceImpl.class);
	
	private ProcessService i_processService;
	private DeviceDao i_deviceDao;

	@Override
	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException {
		SortedMap<DeviceBean, String> devicesAndFiles = new TreeMap<DeviceBean, String>();
		ProcessResult lsscsiProcessResult = getProcessService().run(new ProcessBuilder("lsscsi"), true);
		List<String> lsscsiLines = readStream(new StringReader(lsscsiProcessResult.getOutput()));
		for (DeviceBean deviceBean : getDeviceDao().getAll()) {
			String line = findLineContainingString(lsscsiLines, deviceBean.getIdentifier());
			if (line != null) {
				int openingSquareBracket = line.indexOf('[');
				int closingSquareBracket = line.indexOf(']');
				String scsiDevice = line.substring(openingSquareBracket + 1, closingSquareBracket - openingSquareBracket);
				File scsiDirectory = new File("/sys/bus/scsi/devices/" + scsiDevice + "/block/");
				if (scsiDirectory.isDirectory()) {
					// This directory should contain exactly one file, the block name of the device.
					String[] blockDevices = scsiDirectory.list();
					if (blockDevices.length == 1) {
						String blockFile = "/dev/" + blockDevices[0] + "1";
						log.info("Found " + deviceBean.getFullDescription() + " at location " + blockFile);
						devicesAndFiles.put(deviceBean, blockFile);
					}
				}
			}
		}
		return devicesAndFiles;
	}

	@Override
	public File getMountPointForFile(String path) throws IOException {
		ProcessService processService = getProcessService();
		File mountPoint = findExistingMountPoint(path);
		if (mountPoint != null) {
			if (mountPoint.canWrite()) {
				return mountPoint;
			}
			processService.run(new ProcessBuilder("sudo", "umount", path), true);
		}
		processService.run(new ProcessBuilder("pmount", path), true);
		mountPoint = findExistingMountPoint(path);
		if (mountPoint == null) {
			throw new IOException("Mounting " + path + " failed.");
		}
		return mountPoint;
	}

	@Override
	public boolean mountingRequiresPassword(String path) {
		return false;
	}
	
	protected File findExistingMountPoint(String path) throws IOException {
		FileReader reader = new FileReader(new File("/etc/mtab"));
		List<String> entries = readStream(reader);
		reader.close();
		String entry = findLineContainingString(entries, path);
		return entry==null?null:new File(StringUtils.split(entry, ' ')[1]);
	}
	
	@Override
	public void safelyRemove(File mountPoint) throws IOException {
		getProcessService().run(new ProcessBuilder("pumount", mountPoint.getCanonicalPath()), true);
	}

	protected List<String> readStream(Reader in) throws IOException {
		List<String> lines = new LinkedList<String>();
		String line;
		BufferedReader buff = new BufferedReader(in);
		while ((line = buff.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
	
	protected String findLineContainingString(List<String> lines, final String str) {
		Predicate<String> predicate = new Predicate<String>() {
			@Override
			public boolean evaluate(String line) {
				return line.indexOf(str) != -1;
			}
		};
		return CollectionUtils.find(lines, predicate);
	}

	public ProcessService getProcessService() {
		return i_processService;
	}

	@Required
	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	@Required
	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}
}
