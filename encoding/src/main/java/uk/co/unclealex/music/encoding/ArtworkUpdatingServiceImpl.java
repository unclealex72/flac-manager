package uk.co.unclealex.music.encoding;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import uk.co.unclealex.music.covers.ArtworkManager;
import uk.co.unclealex.music.covers.ArtworkSearchingService;

public class ArtworkUpdatingServiceImpl implements ArtworkUpdatingService {

	private static final Logger log = Logger.getLogger(ArtworkUpdatingServiceImpl.class);
	
	private ArtworkManager i_flacArtworkManager;
	private ArtworkSearchingService i_artworkSearchingService;
	private Map<Encoding, ArtworkManager> i_encodingArtworkManagers;
	
	@Override
	public void updateArtwork(SortedSet<File> flacFiles, SortedSet<File> possibleImageFiles) {
		if (flacFiles.isEmpty()) {
			return;
		}
		byte[] imageData = possibleImageFiles==null?null:findImageData(possibleImageFiles);
		if (imageData == null) {
			updateArtwork(flacFiles);
		}
		else {
			updateArtwork(flacFiles, imageData);
		}
	}

	protected byte[] findImageData(SortedSet<File> possibleImageFiles) {
		byte[] imageData = null;
		for (Iterator<File> iter = possibleImageFiles.iterator(); imageData == null && iter.hasNext(); ) {
			File possibleImageFile = iter.next();
			try {
				BufferedImage img = ImageIO.read(possibleImageFile);
				if (img != null) {
					imageData = toByteArray(new FileInputStream(possibleImageFile));
				}
			}
			catch (IOException e) {
				log.warn("Could not read image data from file " + possibleImageFile, e);
			}
		}
		return imageData;
	}

	protected byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			IOUtils.copy(in, out);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
		return out.toByteArray();
	}

	protected void updateArtwork(SortedSet<File> flacFiles) {
		final ArtworkManager flacArtworkManager = getFlacArtworkManager();
		Predicate<File> hasArtworkPredicate = new Predicate<File>() {
			@Override
			public boolean evaluate(File flacFile) {
				try {
					return flacArtworkManager.artworkExists(flacFile);
				}
				catch (IOException e) {
					log.warn("Could not determine whether flac file " + flacFile + " has artwork embedded in it. Assuming it has not.", e);
					return false;
				}
			}
		};
		SortedSet<File> flacFilesWithoutArtwork = 
			CollectionUtils.select(flacFiles, PredicateUtils.notPredicate(hasArtworkPredicate), new TreeSet<File>());
		if (!flacFilesWithoutArtwork.isEmpty()) {
			try {
				File flacFileWithArtwork = CollectionUtils.find(flacFiles, hasArtworkPredicate);
				byte[] imageData;
				if (flacFileWithArtwork != null) {
					imageData = retrieveImageDataFromFlacFile(flacFileWithArtwork);
				}
				else {
					imageData = retreiveImageDataExternally(flacFilesWithoutArtwork.first());
				}
				if (imageData != null) {
					updateArtwork(flacFilesWithoutArtwork, imageData);
				}
			}
			catch (IOException e) {
				log.warn("Could not read any artwork for flac files " + StringUtils.join(flacFilesWithoutArtwork, ", "), e);
			}
		}
	}

	private byte[] retreiveImageDataExternally(File flacFile) throws IOException {
		List<URL> artworkUrls = getArtworkSearchingService().findArtwork(flacFile);
		byte[] imageData;
		if (artworkUrls.isEmpty()) {
			log.info("No artwork could be found externally.");
			imageData = null;
		}
		else {
			URL url = artworkUrls.get(0);
			log.info("Using image data from url " + url);
			imageData = toByteArray(url.openStream());
		}
		return imageData;
	}

	protected byte[] retrieveImageDataFromFlacFile(File flacFileWithArtwork) throws IOException {
		log.info("Using image data from flac file " + flacFileWithArtwork);
		return getFlacArtworkManager().getArtwork(flacFileWithArtwork);
	}

	protected void updateArtwork(SortedSet<File> flacFiles, byte[] imageData) {
		ArtworkManager flacArtworkManager = getFlacArtworkManager();
		String paths = StringUtils.join(flacFiles, ", ");
		log.info("Updating artwork for files " + paths);
		try {
			flacArtworkManager.setArtwork(imageData, flacFiles.toArray(new File[0]));
		}
		catch (IOException e) {
			log.warn("Could not update artwork for flac file " + paths, e);
		}
	}

	@Override
	public void updateEncodedArtwork(Encoding encoding, File flacFile, File encodedDestination) {
		ArtworkManager artworkManager = getEncodingArtworkManagers().get(encoding);
		if (artworkManager != null) {
			try {
				byte[] imageData = retrieveImageDataFromFlacFile(flacFile);
				if (imageData != null) {
					log.info("Updating artwork for encoded file " + encodedDestination);
					artworkManager.setArtwork(imageData, encodedDestination);
				}
			}
			catch (IOException e) {
				log.warn("Updating artwork for encoded file " + encodedDestination + " failed." , e);
			}
		}
	}

	public ArtworkManager getFlacArtworkManager() {
		return i_flacArtworkManager;
	}

	public void setFlacArtworkManager(ArtworkManager flacArtworkManager) {
		i_flacArtworkManager = flacArtworkManager;
	}

	public ArtworkSearchingService getArtworkSearchingService() {
		return i_artworkSearchingService;
	}

	public void setArtworkSearchingService(ArtworkSearchingService artworkSearchingService) {
		i_artworkSearchingService = artworkSearchingService;
	}

	public Map<Encoding, ArtworkManager> getEncodingArtworkManagers() {
		return i_encodingArtworkManagers;
	}

	public void setEncodingArtworkManagers(Map<Encoding, ArtworkManager> encodingArtworkManagers) {
		i_encodingArtworkManagers = encodingArtworkManagers;
	}

}
