package uk.co.unclealex.music;

import java.text.Normalizer;

import com.google.inject.Inject;

public class LatinServiceImpl implements LatinService {

	@Inject
	protected LatinServiceImpl() {
		// Default constructor
	}
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.LatinService#removeCommonAccents(java.lang.String)
	 */
	public String removeCommonAccents(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{Alnum}\\s]", "");
	}
}
