package uk.co.unclealex.music.albumcover.service;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.music.base.service.ArtworkTaggingException;

public interface ArtworkTaggingService {

	public void updateTag(File albumCoverFile, File trackFile, String trackName, boolean updateTimestamp) throws IOException, ArtworkTaggingException;

}
