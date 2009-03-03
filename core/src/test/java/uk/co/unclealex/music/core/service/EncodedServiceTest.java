package uk.co.unclealex.music.core.service;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections15.CollectionUtils;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.IdentifiableBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.core.CoreSpringTest;
import uk.co.unclealex.music.core.util.IdentifiableTransformer;

public class EncodedServiceTest extends CoreSpringTest {

	private static final String SLAYER = "SLAYER";
	private static final String NAPALM_DEATH = "NAPALM DEATH";
	private static final String HAUNTING_THE_CHAPEL = "HAUNTING THE CHAPEL";
	private static final String MENTALLY_MURDERED = "MENTALLY MURDERED";

	public void testPurgeAlbums() {
		EncodedService encodedService = getEncodedService();
		EncodedArtistDao encodedArtistDao = getEncodedArtistDao();
		EncodedAlbumDao encodedAlbumDao = getEncodedAlbumDao();
		EncodedArtistBean napalmDeath = encodedArtistDao.findByIdentifier(NAPALM_DEATH);
		EncodedArtistBean slayer = encodedService.findOrCreateArtist(SLAYER, "Slayer");
		encodedService.findOrCreateAlbum(napalmDeath, MENTALLY_MURDERED, "Mentally Murdered");
		encodedService.findOrCreateAlbum(slayer, HAUNTING_THE_CHAPEL, "Haunting the Chapel");
		
		int cnt = encodedService.removeEmptyAlbumsAndArtists();
		assertEquals("The wrong number of albums were removed.", 2, cnt);
		check(
				"The wrong albums remained.", 
				encodedAlbumDao, 
				"SCUM", "FROM ENSLAVEMENT TO OBLITERATION", "SPEAK ENGLISH OR DIE", 
				"EXTREME CONDITIONS DEMAND EXTREME RESPONSES", "SOUNDS OF THE ANIMAL KINGDOM KILL TREND SUICIDE");
		check("The wrong artists remained.", encodedArtistDao, NAPALM_DEATH, "SOD", "BRUTAL TRUTH");
	}
	
	public <T extends IdentifiableBean<T,String>> void check(
		String message, KeyedDao<T> dao, String... expectedIdentifiers) {
		Collection<String> actualIdentifiers = CollectionUtils.collect(dao.getAll(), new IdentifiableTransformer<String>()); 
		assertEquals(message, Arrays.asList(expectedIdentifiers), actualIdentifiers);
	}
}
