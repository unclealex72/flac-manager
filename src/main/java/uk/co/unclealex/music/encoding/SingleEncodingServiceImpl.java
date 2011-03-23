package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import uk.co.unclealex.music.Encoding;
import uk.co.unclealex.process.ProcessService;

public class SingleEncodingServiceImpl implements SingleEncodingService {

	private static final Logger log = LoggerFactory.getLogger(SingleEncodingServiceImpl.class);
	
	private ProcessService i_processService;
	private ArtworkUpdatingService i_artworkUpdatingService;
	private DateFormat i_dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	@Inject
	protected SingleEncodingServiceImpl(ProcessService processService, ArtworkUpdatingService artworkUpdatingService) {
		super();
		i_processService = processService;
		i_artworkUpdatingService = artworkUpdatingService;
	}

	@Override
	public void encode(Encoding encoding, File flacFile, File encodingScript, File encodedDestination) throws EncodingException {
		log.info("Encoding " + flacFile + " to " + encodedDestination);
		if (encodedDestination.exists()) {
			DateFormat dateFormat = getDateFormat();
			log.info(
					"Encoding as " + flacFile + " (" + dateFormat.format(new Date(flacFile.lastModified())) + 
					") is newer than " + encodedDestination + " (" + dateFormat.format(new Date(encodedDestination.lastModified())) + ").");
		}
		else {
			encodedDestination.getParentFile().mkdirs();
			log.info("Encoding as " + encodedDestination + " does not exist.");
		}
		try {
			String encodedDestinationPath = encodedDestination.getCanonicalPath();
			File tempFile = new File(encodedDestinationPath + ".part");
			ProcessBuilder processBuilder = new ProcessBuilder(
					encodingScript.getCanonicalPath(), flacFile.getCanonicalPath(), tempFile.getCanonicalPath());
			getProcessService().run(processBuilder, true);
			if (encodedDestination.exists()) {
				encodedDestination.delete();
			}
			tempFile.renameTo(encodedDestination);
			getArtworkUpdatingService().updateEncodedArtwork(encoding, flacFile, encodedDestination);
		}
		catch (IOException e) {
			encodedDestination.delete();
			throw new EncodingException("File " + encodedDestination + " could not be created.", e);
		}
	}

	public ProcessService getProcessService() {
		return i_processService;
	}

	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}

	public ArtworkUpdatingService getArtworkUpdatingService() {
		return i_artworkUpdatingService;
	}

	public void setArtworkUpdatingService(ArtworkUpdatingService artworkUpdatingService) {
		i_artworkUpdatingService = artworkUpdatingService;
	}

	public DateFormat getDateFormat() {
		return i_dateFormat;
	}

}
