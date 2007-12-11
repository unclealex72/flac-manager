package uk.co.unclealex.music.core.initialise;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.TrackStreamService;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

@Transactional(propagation=Propagation.REQUIRES_NEW)
public class TrackImporterImpl implements TrackImporter {

	private static final Logger log = Logger.getLogger(TrackImporterImpl.class);
	
	private TrackStreamService i_trackStreamService;
	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public void importTrack(EncoderBean encoderBean, File file, FlacTrackBean flacTrackBean) throws IOException {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		FileInputStream in = new FileInputStream(file);
		EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setFlacUrl(flacTrackBean.getUrl());
		encodedTrackBean.setTimestamp(file.lastModified());
		encodedTrackBean.setLength((int) file.length());
		encodedTrackDao.store(encodedTrackBean);
		OutputStream out = getTrackStreamService().getTrackOutputStream(encodedTrackBean);
		encodedTrackBean.setLength(IOUtils.copy(in, out));
		in.close();
		out.close();
		encodedTrackDao.store(encodedTrackBean);
		log.info("Stored " + encoderBean.getExtension() + " of " + flacTrackBean.getUrl());
		encodedTrackDao.clear();
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public TrackStreamService getTrackStreamService() {
		return i_trackStreamService;
	}

	public void setTrackStreamService(TrackStreamService trackStreamService) {
		i_trackStreamService = trackStreamService;
	}

}
