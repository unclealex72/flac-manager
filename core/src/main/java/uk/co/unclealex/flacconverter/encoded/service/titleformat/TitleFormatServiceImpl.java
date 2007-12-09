package uk.co.unclealex.flacconverter.encoded.service.titleformat;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.flacconverter.SlimServerConfig;
import uk.co.unclealex.flacconverter.encoded.model.EncodedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.substitutor.Substitutor;

public class TitleFormatServiceImpl implements TitleFormatService {

	private static String INVALID_CHARACTER_STRING = "/\\?%*:|\"<>";
	protected static final List<String> INVALID_CHARACTERS;
	static {
		List<String> invalidCharacters = new LinkedList<String>();
		for (Character c : INVALID_CHARACTER_STRING.toCharArray()) {
			invalidCharacters.add(c.toString());
		}
		INVALID_CHARACTERS = Collections.unmodifiableList(invalidCharacters);
	}

	private String i_titleFormat;
	private SlimServerConfig i_slimServerConfig;

	public String getTitle(EncodedTrackBean trackBean) {
		Substitutor substitutor = new Substitutor(getTitleFormat());
		EncodedAlbumBean albumBean = trackBean.getEncodedAlbumBean();
		EncodedArtistBean artistBean = albumBean.getEncodedArtistBean();
		substitutor.substitute(TitleFormatVariable.TRACK, trackBean.getTrackNumber());
		substitutor.substitute(TitleFormatVariable.TITLE, sanitise(trackBean.getTitle()));
		substitutor.substitute(TitleFormatVariable.ALBUM, sanitise(albumBean.getTitle()));
		substitutor.substitute(TitleFormatVariable.ARTIST, sanitise(removeDefiniteArticle(artistBean.getName())));
		substitutor.substitute(TitleFormatVariable.EXTENSION, trackBean.getEncoderBean().getExtension());
		return substitutor.getText();
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
	
	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
	}

	public SlimServerConfig getSlimServerConfig() {
		return i_slimServerConfig;
	}

	public void setSlimServerConfig(SlimServerConfig slimServerConfig) {
		i_slimServerConfig = slimServerConfig;
	}

}
