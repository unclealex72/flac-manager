package uk.co.unclealex.music.albumcover.service;

import java.io.IOException;
import java.util.SortedSet;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import uk.co.unclealex.music.base.model.ExternalCoverArtImage;
import uk.co.unclealex.music.base.service.ExternalCoverArtException;
import uk.co.unclealex.music.base.service.ExternalCoverArtService;
import uk.co.unclealex.music.test.SpringTest;

public class AmazonTest extends SpringTest {

	public void testANightAtTheOpera() throws ExternalCoverArtException, IOException {
		ExternalCoverArtService externalCoverArtService = new AmazonExternalCoverArtService();
		getApplicationContext().getAutowireCapableBeanFactory().autowireBeanProperties(externalCoverArtService, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		SortedSet<ExternalCoverArtImage> images = externalCoverArtService.searchForImages("QUEEN", "A NIGHT AT THE OPERA");
		assertFalse("No images were found for A Night At The Opera.", images.size() == 0);
	}
	@Override
	protected String[] getConfigLocations() {
		return new String[] {
			"classpath*:applicationContext-music-core.xml",
			"classpath*:applicationContext-music-test.xml",
			"classpath*:applicationContext-music-test-flac.xml",
			"classpath*:applicationContext-music-album-covers.xml"
		};
	}
}
