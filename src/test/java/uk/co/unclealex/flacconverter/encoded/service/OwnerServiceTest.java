package uk.co.unclealex.flacconverter.encoded.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import uk.co.unclealex.flacconverter.encoded.EncodedSpringTest;
import uk.co.unclealex.flacconverter.encoded.dao.OwnerDao;
import uk.co.unclealex.flacconverter.encoded.model.OwnedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;

public class OwnerServiceTest extends EncodedSpringTest {

	private OwnerDao i_ownerDao;
	private OwnerService i_ownerService;

	public void testOwnedAlbums() {
		OwnerBean ownerBean = getOwnerDao().getAll().first();
		addOwnedAlbum(ownerBean, "SCUM", "NAPALM DEATH");
		addOwnedArtist(ownerBean, "BRUTAL TRUTH");
		getOwnerDao().store(ownerBean);
		
		SortedSet<FlacAlbumBean> flacAlbumBeans = getOwnerService().getOwnedAlbums(ownerBean);
		List<String> actual = new LinkedList<String>();
		for (FlacAlbumBean flacAlbumBean : flacAlbumBeans) {
			actual.add(flacAlbumBean.getFlacArtistBean().getName());
			actual.add(flacAlbumBean.getTitle());
		}
		String[] expected = {
				"Brutal Truth", "Extreme Conditions Demand Extreme Responses",
				"Brutal Truth", "Sounds Of The Animal Kingdomkill Trend Suicide",
				"Napalm Death", "Scum"
		};
		
		assertEquals("The wrong albums were returned.", Arrays.asList(expected),  actual);
	}

	protected void addOwnedAlbum(OwnerBean ownerBean, String albumName, String artistName) {
		OwnedAlbumBean ownedAlbumBean = new OwnedAlbumBean();
		ownedAlbumBean.setOwnerBean(ownerBean);
		ownedAlbumBean.setAlbumName(albumName);
		ownedAlbumBean.setArtistName(artistName);
		ownerBean.getOwnedAlbumBeans().add(ownedAlbumBean);
	}
	
	protected void addOwnedArtist(OwnerBean ownerBean, String artistName) {
		OwnedArtistBean ownedArtistBean = new OwnedArtistBean();
		ownedArtistBean.setOwnerBean(ownerBean);
		ownedArtistBean.setName(artistName);
		ownerBean.getOwnedArtistBeans().add(ownedArtistBean);
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}
}
