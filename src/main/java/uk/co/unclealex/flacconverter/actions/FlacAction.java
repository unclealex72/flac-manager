package uk.co.unclealex.flacconverter.actions;

import java.io.IOException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.dao.OwnerDao;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;
import uk.co.unclealex.flacconverter.encoded.service.DeviceService;
import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;
import uk.co.unclealex.flacconverter.flac.dao.FlacArtistDao;
import uk.co.unclealex.flacconverter.flac.model.DownloadCartBean;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.flac.service.DownloadCartService;
import uk.co.unclealex.flacconverter.interceptor.AfterPreparable;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class FlacAction extends ActionSupport implements Preparable, AfterPreparable {

	private DeviceService i_deviceService;
	private EncoderDao i_encoderDao;
	private WritingListenerService i_writingListenerService;
	private FlacArtistDao i_flacArtistDao;
	private OwnerDao i_ownerDao;
	private DownloadCartService i_downloadCartService;
	private SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>> i_downloadCartContents;
	private SortedSet<DeviceBean> i_connectedDevices;
	private SortedSet<Character> i_startingCharacters;
	private SortedSet<OwnerBean> i_owners;
	private SortedSet<EncoderBean> i_encoders;
	private DownloadCartBean i_sessionDownloadCartBean;
	
	@Override
	public final void prepare() throws Exception {
		prepareDevices();
		prepareLetters();
		prepareOwners();
		prepareEncoders();
		doPrepare();
	}

	private void prepareDevices() throws IOException {
		Set<DeviceBean> deviceBeans = getDeviceService().findDevicesAndFiles().keySet();
		SortedSet<DeviceBean> connectedDevices = new TreeSet<DeviceBean>(deviceBeans);
		connectedDevices.removeAll(getWritingListenerService().getAllListeners().keySet());
		setConnectedDevices(connectedDevices);		
	}
	
	private void prepareLetters() {
		FlacArtistDao flacArtistDao = getFlacArtistDao();
		SortedSet<Character> startingCharacters = new TreeSet<Character>();
		for (char c = 'A'; c <= 'Z'; c++) {
			if (flacArtistDao.countArtistsBeginningWith(c) != 0) {
				startingCharacters.add(c);
			}
		}
		setStartingCharacters(startingCharacters);		
	}
	
	private void prepareOwners() {
		setOwners(getOwnerDao().getAll());		
	}
	
	private void prepareEncoders() {
		setEncoders(getEncoderDao().getAll());		
	}
	
	public void prepareAfter() {
		SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>> downloadCartContents =
			getDownloadCartService().createFullView(getDownloadCartBean());
		setDownloadCartContents(downloadCartContents);
	}
	
	protected void doPrepare() throws Exception {
		// Subclasses should override this if neccessary.
	}
	
	public DownloadCartBean getDownloadCartBean() {
		DownloadCartBean downloadCartBean = getSessionDownloadCartBean();
		if (downloadCartBean == null) {
			downloadCartBean = new DownloadCartBean();
			setSessionDownloadCartBean(downloadCartBean);
		}
		return downloadCartBean;
	}
	
	@Required
	public DeviceService getDeviceService() {
		return i_deviceService;
	}

	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public SortedSet<DeviceBean> getConnectedDevices() {
		return i_connectedDevices;
	}

	public void setConnectedDevices(SortedSet<DeviceBean> deviceBeans) {
		i_connectedDevices = deviceBeans;
	}

	public WritingListenerService getWritingListenerService() {
		return i_writingListenerService;
	}

	public void setWritingListenerService(
			WritingListenerService writingListenerService) {
		i_writingListenerService = writingListenerService;
	}

	public SortedSet<Character> getStartingCharacters() {
		return i_startingCharacters;
	}

	public void setStartingCharacters(SortedSet<Character> startingCharacters) {
		i_startingCharacters = startingCharacters;
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	public SortedSet<OwnerBean> getOwners() {
		return i_owners;
	}

	public void setOwners(SortedSet<OwnerBean> owners) {
		i_owners = owners;
	}
	
	public DownloadCartBean getSessionDownloadCartBean() {
		return i_sessionDownloadCartBean;
	}

	public void setSessionDownloadCartBean(DownloadCartBean sessionDownloadCartBean) {
		i_sessionDownloadCartBean = sessionDownloadCartBean;
	}

	public DownloadCartService getDownloadCartService() {
		return i_downloadCartService;
	}

	public void setDownloadCartService(DownloadCartService downloadCartService) {
		i_downloadCartService = downloadCartService;
	}

	public SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>> getDownloadCartContents() {
		return i_downloadCartContents;
	}

	public void setDownloadCartContents(
			SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>> downloadCartContents) {
		i_downloadCartContents = downloadCartContents;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public SortedSet<EncoderBean> getEncoders() {
		return i_encoders;
	}

	public void setEncoders(SortedSet<EncoderBean> encoders) {
		i_encoders = encoders;
	}
}
