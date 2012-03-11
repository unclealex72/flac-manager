package uk.co.unclealex.music.legacy.encoding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.legacy.Encoding;
import uk.co.unclealex.music.legacy.covers.ArtworkManager;
import uk.co.unclealex.music.legacy.covers.ArtworkSearchingService;
import uk.co.unclealex.music.legacy.inject.EncodingArtworkManagers;
import uk.co.unclealex.music.legacy.inject.FlacArtworkManager;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;

public class ArtworkUpdatingServiceImpl implements ArtworkUpdatingService {

	private static final Logger log = LoggerFactory.getLogger(ArtworkUpdatingServiceImpl.class);
	
	private ArtworkManager i_flacArtworkManager;
	private ArtworkSearchingService i_artworkSearchingService;
	private Map<Encoding, ArtworkManager> i_encodingArtworkManagers;
	private ImageService i_imageService;
	
	@Inject
	protected ArtworkUpdatingServiceImpl(
			@FlacArtworkManager ArtworkManager flacArtworkManager,
			ArtworkSearchingService artworkSearchingService,
			@EncodingArtworkManagers Map<Encoding, ArtworkManager> encodingArtworkManagers,
			ImageService imageService) {
		super();
		i_flacArtworkManager = flacArtworkManager;
		i_artworkSearchingService = artworkSearchingService;
		i_encodingArtworkManagers = encodingArtworkManagers;
		i_imageService = imageService;
	}

	@Override
	public boolean updateArtwork(SortedSet<File> flacFiles, SortedSet<File> possibleImageFiles) {
		if (flacFiles.isEmpty()) {
			return false;
		}
		byte[] imageData = possibleImageFiles==null?null:findImageData(possibleImageFiles);
		if (imageData == null) {
			return updateArtwork(flacFiles);
		}
		else {
			return updateArtwork(flacFiles, imageData);
		}
	}

	protected byte[] findImageData(SortedSet<File> possibleImageFiles) {
		byte[] imageData = null;
		ImageService imageService = getImageService();
		for (Iterator<File> iter = possibleImageFiles.iterator(); imageData == null && iter.hasNext(); ) {
			File possibleImageFile = iter.next();
			try {
				imageData = imageService.loadImage(possibleImageFile);
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
			ByteStreams.copy(in, out);
		}
		finally {
			Closeables.closeQuietly(in);
		}
		return out.toByteArray();
	}

	protected boolean updateArtwork(SortedSet<File> flacFiles) {
		final ArtworkManager flacArtworkManager = getFlacArtworkManager();
		Predicate<File> hasArtworkPredicate = new Predicate<File>() {
			@Override
			public boolean apply(File flacFile) {
				try {
					return flacArtworkManager.artworkExists(flacFile);
				}
				catch (IOException e) {
					log.warn("Could not determine whether flac file " + flacFile + " has artwork embedded in it. Assuming it has not.", e);
					return false;
				}
			}
		};
		SortedSet<File> flacFilesWithoutArtwork = Sets.newTreeSet(Iterables.filter(flacFiles, Predicates.not(hasArtworkPredicate))); 
		if (!flacFilesWithoutArtwork.isEmpty()) {
			try {
				File flacFileWithArtwork = Iterables.find(flacFiles, hasArtworkPredicate, null);
				byte[] imageData;
				if (flacFileWithArtwork != null) {
					imageData = retrieveImageDataFromFlacFile(flacFileWithArtwork);
				}
				else {
					imageData = retreiveImageDataExternally(flacFilesWithoutArtwork.first());
				}
				if (imageData != null) {
					return updateArtwork(flacFilesWithoutArtwork, imageData);
				}
			}
			catch (IOException e) {
				log.warn("Could not read any artwork for flac files " + Joiner.on(", ").join(flacFilesWithoutArtwork), e);
			}
			return false;
		}
		return true;
	}

	private byte[] retreiveImageDataExternally(File flacFile) throws IOException {
		List<String> artworkUrls = getArtworkSearchingService().findArtwork(flacFile);
		byte[] imageData;
		if (artworkUrls.isEmpty()) {
			log.info("No artwork could be found externally.");
			imageData = null;
		}
		else {
			URL url = new URL(artworkUrls.get(0));
			log.info("Using image data from url " + url);
			imageData = toByteArray(url.openStream());
		}
		return imageData;
	}

	protected byte[] retrieveImageDataFromFlacFile(File flacFileWithArtwork) throws IOException {
		log.info("Using image data from flac file " + flacFileWithArtwork);
		return getFlacArtworkManager().getArtwork(flacFileWithArtwork);
	}

	protected boolean updateArtwork(SortedSet<File> flacFiles, byte[] imageData) {
		ArtworkManager flacArtworkManager = getFlacArtworkManager();
		String paths = Joiner.on(", ").join(flacFiles);
		log.info("Updating artwork for files " + paths);
		try {
			flacArtworkManager.setArtwork(imageData, flacFiles.toArray(new File[0]));
			return true;
		}
		catch (IOException e) {
			log.warn("Could not update artwork for flac file " + paths, e);
			return false;
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

	public ArtworkSearchingService getArtworkSearchingService() {
		return i_artworkSearchingService;
	}

	public Map<Encoding, ArtworkManager> getEncodingArtworkManagers() {
		return i_encodingArtworkManagers;
	}

	public ImageService getImageService() {
		return i_imageService;
	}
}
