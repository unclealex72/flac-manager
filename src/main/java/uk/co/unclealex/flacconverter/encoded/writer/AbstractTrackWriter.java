package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.flacconverter.SlimServerConfig;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.substitutor.Substitutor;

public abstract class AbstractTrackWriter<T extends OutputStream> implements TrackWriter {

	private static String INVALID_CHARACTER_STRING = "/\\?%*:|\"<>";
	protected static final List<String> INVALID_CHARACTERS;
	static {
		List<String> invalidCharacters = new LinkedList<String>();
		for (Character c : INVALID_CHARACTER_STRING.toCharArray()) {
			invalidCharacters.add(c.toString());
		}
		INVALID_CHARACTERS = Collections.unmodifiableList(invalidCharacters);
	}
	
	private FlacTrackDao i_flacTrackDao;
	private SlimServerConfig i_slimServerConfig;
	
	@Override
	public final String write(EncodedTrackBean encodedTrackBean, String titleFormat) throws IOException {
		Substitutor substitutor = new Substitutor(titleFormat);
		FlacTrackBean flacTrackBean = getFlacTrackDao().findByUrl(encodedTrackBean.getFlacUrl());
		FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
		FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
		substitutor.substitute("track", flacTrackBean.getTrackNumber());
		substitutor.substitute("title", sanitise(flacTrackBean.getTitle()));
		substitutor.substitute("album", sanitise(flacAlbumBean.getTitle()));
		substitutor.substitute("artist", sanitise(removeDefiniteArticle(flacArtistBean.getName())));
		String title = substitutor.getText() + "." + encodedTrackBean.getEncoderBean().getExtension();
		T out = createStream(encodedTrackBean, title);
		TrackDataBean trackDataBean = encodedTrackBean.getTrackDataBean();
		out.write(trackDataBean.getTrack());
		closeStream(encodedTrackBean, title, out);
		return title;
	}
	
	protected String sanitise(String str) {
		for (String c : INVALID_CHARACTERS) {
			str = StringUtils.replace(str, c, "");
		}
		return str;
	}
	
	protected String removeDefiniteArticle(String artist) {
		List<String> definiteArticles = getSlimServerConfig().getDefiniteArticles();
		if (definiteArticles == null) {
			return artist;
		}
		for (String article : definiteArticles) {
			article = article.trim();
			int toRemove = article.length() + 1;
			if (artist.startsWith(article) && artist.length() > toRemove && 
					Character.isWhitespace(artist.charAt(toRemove - 1))) {
				return artist.substring(toRemove).trim();
			}
		}
		return artist;
	}

	public abstract T createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException;
	
	public abstract void closeStream(EncodedTrackBean encodedTrackBean, String title, T out) throws IOException;
	
	@Required
	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	@Required
	public SlimServerConfig getSlimServerConfig() {
		return i_slimServerConfig;
	}

	public void setSlimServerConfig(SlimServerConfig slimServerConfig) {
		i_slimServerConfig = slimServerConfig;
	}
}
