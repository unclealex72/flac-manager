package uk.co.unclealex.music.core.service.filesystem;

import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.core.model.EncodedArtistBean;

public class FirstLetterOfArtistTransformer implements
		Transformer<EncodedArtistBean, String> {

	@Override
	public String transform(EncodedArtistBean encodedArtistBean) {
		return encodedArtistBean.getIdentifier().substring(0, 1);
	}
}
