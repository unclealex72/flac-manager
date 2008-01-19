package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.music.core.writer.NoOpWritingListener;
import uk.co.unclealex.music.core.writer.TrackStream;
import uk.co.unclealex.music.core.writer.TrackWriter;
import uk.co.unclealex.music.core.writer.TrackWriterFactory;
import uk.co.unclealex.music.core.writer.TrackWritingException;
import uk.co.unclealex.music.core.writer.WritingListener;
import uk.co.unclealex.spring.Prototype;

@Prototype
@Transactional
public class DevicesWriterImpl implements DevicesWriter {

	private static final Logger log = Logger.getLogger(DevicesWriterImpl.class);
	
	private OwnerService i_ownerService;
	private TrackWriterFactory i_trackWriterFactory;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private DeviceService i_deviceService;
	
	private Map<DeviceBean, File> i_deviceDirectories = new TreeMap<DeviceBean, File>();
	private Map<DeviceBean, Collection<WritingListener>> i_writingListeners = 
		new TreeMap<DeviceBean, Collection<WritingListener>>();
	
	@Override
	public void addDevice(DeviceBean deviceBean, File deviceDirectory,
			Collection<WritingListener> writingListeners) {
		getDeviceDirectories().put(deviceBean, deviceDirectory);
		getWritingListeners().put(deviceBean, writingListeners);
	}

	@Override
	public void write() throws TrackWritingException {
		TrackWriterFactory factory = getTrackWriterFactory();
		OwnerService ownerService = getOwnerService();
		TitleFormatServiceFactory titleFormatServiceFactory = getTitleFormatServiceFactory();
		Map<TrackStream, TitleFormatService> titleFormatServicesByTrackStream = 
			new HashMap<TrackStream, TitleFormatService>();
		Map<DeviceBean, Integer> fileCountsByDeviceBean = new HashMap<DeviceBean, Integer>();
		SortedMap<EncodedTrackBean, Collection<TrackStream>> trackStreamsByTrack =
			new TreeMap<EncodedTrackBean, Collection<TrackStream>>();
		Map<DeviceBean, TrackStream> trackStreamsByDeviceBean = new HashMap<DeviceBean, TrackStream>();
		
		for (Map.Entry<DeviceBean, File> entry : getDeviceDirectories().entrySet()) {
			DeviceBean deviceBean = entry.getKey();
			TrackStream trackStream = factory.createFileTrackStream(entry.getValue());
			trackStreamsByDeviceBean.put(deviceBean, trackStream);
			titleFormatServicesByTrackStream.put(trackStream, titleFormatServiceFactory.createTitleFormatService(deviceBean));
			SortedSet<EncodedTrackBean> encodedTrackBeans = 
				ownerService.getOwnedEncodedTracks(deviceBean.getOwnerBean(), deviceBean.getEncoderBean());
			fileCountsByDeviceBean.put(deviceBean, encodedTrackBeans.size());
			for (EncodedTrackBean encodedTrackBean : encodedTrackBeans) {
				Collection<TrackStream> trackStreamsForTrack = trackStreamsByTrack.get(encodedTrackBean);
				if (trackStreamsForTrack == null) {
					trackStreamsForTrack = new LinkedList<TrackStream>();
					trackStreamsByTrack.put(encodedTrackBean, trackStreamsForTrack);
				}
				trackStreamsForTrack.add(trackStream);
			}
		}
		
		TrackWriter trackWriter = factory.createTrackWriter(titleFormatServicesByTrackStream);
		for (Map.Entry<DeviceBean, Collection<WritingListener>> entry : getWritingListeners().entrySet()) {
			DeviceBean deviceBean = entry.getKey();
			TrackStream trackStream = trackStreamsByDeviceBean.get(deviceBean);
			for (WritingListener writingListener : entry.getValue()) {
				writingListener.initialise(fileCountsByDeviceBean.get(deviceBean));
				trackWriter.registerWritingListener(writingListener, trackStream);
			}
			trackWriter.registerWritingListener(
					new DeviceWritingListener(deviceBean, getDeviceDirectories().get(deviceBean)), trackStream);
		}
		trackWriter.initialise(titleFormatServicesByTrackStream);
		trackWriter.create();
		trackWriter.writeAll(trackStreamsByTrack);
		trackWriter.close();
	}

	protected class DeviceWritingListener extends NoOpWritingListener {
		private File mountPoint;
		private DeviceBean deviceBean;
		private List<String> titlesWritten = new LinkedList<String>();
		
		public DeviceWritingListener(DeviceBean deviceBean, File mountPoint) {
			super();
			this.mountPoint = mountPoint;
			this.deviceBean = deviceBean;
		}
		
		@Override
		public void beforeFileWrites() throws IOException {
			getDeviceService().removeMusicFolders(deviceBean, mountPoint);
		}
		
		@Override
		public void registerFileWritten(String track, int length) {
			titlesWritten.add(track);
		}
		
		@Override
		public void registerFileIgnore(String title) {
			titlesWritten.add(title);
		}
		
		@Override
		public void afterFilesWritten() throws IOException {
			if (!deviceBean.isDeletingRequired()) {
				new StaleFileRemover(titlesWritten, mountPoint, deviceBean.getEncoderBean().getExtension()).removeAll();
			}
		}
		
		@Override
		public void finish(IOException exception) {
			safelyRemove();
		}
		@Override
		public void finish() {
			safelyRemove();
		}
		protected void safelyRemove() {
			try {
				getDeviceService().safelyRemove(mountPoint);
			}
			catch (IOException e) {
				log.warn("Could not safely remove the device on " + mountPoint, e);
			}
		}
	}

	public Map<DeviceBean, File> getDeviceDirectories() {
		return i_deviceDirectories;
	}

	public void setDeviceDirectories(Map<DeviceBean, File> deviceDirectories) {
		i_deviceDirectories = deviceDirectories;
	}

	public Map<DeviceBean, Collection<WritingListener>> getWritingListeners() {
		return i_writingListeners;
	}

	public void setWritingListeners(
			Map<DeviceBean, Collection<WritingListener>> writingListeners) {
		i_writingListeners = writingListeners;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	@Required
	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public TrackWriterFactory getTrackWriterFactory() {
		return i_trackWriterFactory;
	}

	@Required
	public void setTrackWriterFactory(TrackWriterFactory trackWriterFactory) {
		i_trackWriterFactory = trackWriterFactory;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	@Required
	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	@Required
	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

}
