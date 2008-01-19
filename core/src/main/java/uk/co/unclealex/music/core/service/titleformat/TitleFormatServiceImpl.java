package uk.co.unclealex.music.core.service.titleformat;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.OwnerBean;
import uk.co.unclealex.music.core.substitutor.Substitutor;
import uk.co.unclealex.spring.Prototype;

@Prototype
public class TitleFormatServiceImpl implements TitleFormatService {

	private String i_titleFormat;
	private boolean i_ownerRequired;
	
	public String getTitle(EncodedTrackBean encodedTrackBean) {
		return getTitle(encodedTrackBean, null);
	}
	
	public String getTitle(EncodedTrackBean trackBean, OwnerBean ownerBean) {
		Substitutor substitutor = new Substitutor(getTitleFormat());
		EncodedAlbumBean albumBean = trackBean.getEncodedAlbumBean();
		EncodedArtistBean artistBean = albumBean.getEncodedArtistBean();
		substitutor.substitute(TitleFormatVariable.TRACK, trackBean.getTrackNumber());
		substitutor.substitute(TitleFormatVariable.TITLE, trackBean.getFilename());
		substitutor.substitute(TitleFormatVariable.ALBUM, albumBean.getFilename());
		substitutor.substitute(TitleFormatVariable.ARTIST, artistBean.getFilename());
		substitutor.substitute(TitleFormatVariable.EXTENSION, trackBean.getEncoderBean().getExtension());
		if (ownerBean != null && isOwnerRequired()) {
			substitutor.substitute(TitleFormatVariable.OWNER, ownerBean.getName());
		}
		return substitutor.getText();
	}

	protected Substitutor createSubstitutor() {
		return new Substitutor(getTitleFormat());
	}
	
	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
		Substitutor substitutor = createSubstitutor();
		setOwnerRequired(substitutor.isTitleFormatVariableRequired(TitleFormatVariable.OWNER));
	}

	public boolean isOwnerRequired() {
		return i_ownerRequired;
	}

	public void setOwnerRequired(boolean ownerRequired) {
		i_ownerRequired = ownerRequired;
	}
}
