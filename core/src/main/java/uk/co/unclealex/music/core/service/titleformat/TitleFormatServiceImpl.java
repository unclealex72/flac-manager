package uk.co.unclealex.music.core.service.titleformat;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.spring.Prototype;
import uk.co.unclealex.music.core.substitutor.Substitutor;

@Prototype
public class TitleFormatServiceImpl implements TitleFormatService {

	private String i_titleFormat;

	public String getTitle(EncodedTrackBean trackBean) {
		Substitutor substitutor = new Substitutor(getTitleFormat());
		EncodedAlbumBean albumBean = trackBean.getEncodedAlbumBean();
		EncodedArtistBean artistBean = albumBean.getEncodedArtistBean();
		substitutor.substitute(TitleFormatVariable.TRACK, trackBean.getTrackNumber());
		substitutor.substitute(TitleFormatVariable.TITLE, trackBean.getFilename());
		substitutor.substitute(TitleFormatVariable.ALBUM, albumBean.getFilename());
		substitutor.substitute(TitleFormatVariable.ARTIST, artistBean.getFilename());
		substitutor.substitute(TitleFormatVariable.EXTENSION, trackBean.getEncoderBean().getExtension());
		return substitutor.getText();
	}

	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
	}
}
