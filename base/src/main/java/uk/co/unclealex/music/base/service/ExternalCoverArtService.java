package uk.co.unclealex.music.base.service;

import java.io.IOException;
import java.util.SortedSet;

import uk.co.unclealex.music.base.model.ExternalCoverArtImage;

public interface ExternalCoverArtService {

	public SortedSet<ExternalCoverArtImage> searchForImages(String artist, String album) throws ExternalCoverArtException, IOException;
}
