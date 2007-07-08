package uk.co.unclealex.flacconverter.encoded.transformer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public class SpringToEncodedTracksTransformerFactory implements
		ToEncodedTracksTransformerFactory, ApplicationContextAware {

	private String i_flacAlbumToEncodedTracksTransformerId = "flacAlbumToEncodedTracksTransformerId";
	private String i_flacTrackToEncodedTrackTransformerId = "flacTrackToEncodedTrackTransformerId";
	private ApplicationContext i_applicationContext;
	
	@Override
	public FlacAlbumToEncodedTracksTransformer createFlacAlbumToEncodedTracksTransformer(
			EncoderBean encoderBean) {
		FlacAlbumToEncodedTracksTransformer transformer = 
			(FlacAlbumToEncodedTracksTransformer) getApplicationContext().getBean(getFlacAlbumToEncodedTracksTransformerId());
		transformer.setEncoderBean(encoderBean);
		return transformer;
	}

	@Override
	public FlacTrackToEncodedTrackTransformer createFlacTrackToEncodedTrackTransformer(
			EncoderBean encoderBean) {
		FlacTrackToEncodedTrackTransformer transformer =
			(FlacTrackToEncodedTrackTransformer) getApplicationContext().getBean(getFlacTrackToEncodedTrackTransformerId());
		transformer.setEncoderBean(encoderBean);
		return transformer;
	}

	public String getFlacAlbumToEncodedTracksTransformerId() {
		return i_flacAlbumToEncodedTracksTransformerId;
	}

	public void setFlacAlbumToEncodedTracksTransformerId(
			String flacAlbumToEncodedTracksTransformerId) {
		i_flacAlbumToEncodedTracksTransformerId = flacAlbumToEncodedTracksTransformerId;
	}

	public String getFlacTrackToEncodedTrackTransformerId() {
		return i_flacTrackToEncodedTrackTransformerId;
	}

	public void setFlacTrackToEncodedTrackTransformerId(
			String flacTrackToEncodedTrackTransformerId) {
		i_flacTrackToEncodedTrackTransformerId = flacTrackToEncodedTrackTransformerId;
	}

	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

}
