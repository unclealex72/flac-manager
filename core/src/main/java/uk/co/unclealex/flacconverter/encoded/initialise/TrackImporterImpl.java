package uk.co.unclealex.flacconverter.encoded.initialise;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.service.SingleEncoderService;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

@Transactional(propagation=Propagation.REQUIRES_NEW)
public class TrackImporterImpl implements TrackImporter {

	private static final Logger log = Logger.getLogger(TrackImporterImpl.class);
	
	private SingleEncoderService i_singleEncoderService;
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
		OutputStream out = getSingleEncoderService().getTrackOutputStream(encodedTrackBean);
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

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}

}
