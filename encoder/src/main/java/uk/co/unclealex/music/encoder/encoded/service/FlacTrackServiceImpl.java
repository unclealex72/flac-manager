package uk.co.unclealex.music.encoder.encoded.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.service.EncodedService;
import uk.co.unclealex.music.encoder.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.model.FlacArtistBean;

@Service
@Transactional
public class FlacTrackServiceImpl implements FlacTrackService {

	private EncodedService i_encodedService;
	
	@Override
	public EncodedAlbumBean findOrCreateEncodedAlbumBean(FlacAlbumBean flacAlbumBean) {
		FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
		EncodedService encodedService = getEncodedService();
		EncodedArtistBean encodedArtistBean = 
			encodedService.findOrCreateArtist(flacArtistBean.getCode(), flacArtistBean.getName());
		return
			encodedService.findOrCreateAlbum(encodedArtistBean, flacAlbumBean.getCode(), flacAlbumBean.getTitle());
}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

}
