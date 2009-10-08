package uk.co.unclealex.music.core.service.titleformat;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.substitutor.Substitutor;

public class TitleFormatServiceImpl implements TitleFormatService {

	private String i_titleFormat;
	
	@Override
	public String createTitle(EncodedTrackBean encodedTrackBean, OwnerBean ownerBean) {
		Substitutor substitutor = new Substitutor(getTitleFormat());
		Integer trackNumber = encodedTrackBean.getTrackNumber();
		String title = encodedTrackBean.getTitle();
		EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
		String album = encodedAlbumBean.getFilename();
		String artist = encodedAlbumBean.getEncodedArtistBean().getFilename();
		String extension = encodedTrackBean.getEncoderBean().getExtension();
		String owner = ownerBean.getName();

		substitutor.substitute(TitleFormatVariable.TRACK, trackNumber);
		substitutor.substitute(TitleFormatVariable.TITLE, title);
		substitutor.substitute(TitleFormatVariable.ALBUM, album);
		substitutor.substitute(TitleFormatVariable.ARTIST, artist);
		substitutor.substitute(TitleFormatVariable.EXTENSION, extension);
		substitutor.substitute(TitleFormatVariable.OWNER, owner);
		return substitutor.getText();
	}

	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
	}
}
