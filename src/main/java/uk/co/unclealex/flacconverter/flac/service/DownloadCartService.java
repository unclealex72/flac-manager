package uk.co.unclealex.flacconverter.flac.service;

import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.flac.model.DownloadCartBean;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public interface DownloadCartService {

	public SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>>
		createFullView(DownloadCartBean downloadCartBean);
	
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans(DownloadCartBean downloadCartBean);
}
