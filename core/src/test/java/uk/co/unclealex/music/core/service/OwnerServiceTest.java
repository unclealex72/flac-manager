package uk.co.unclealex.music.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.core.CoreSpringTest;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.model.OwnerBean;

public class OwnerServiceTest extends CoreSpringTest {

	private OwnerService i_ownerService;
	
	public void testOwnedEncodedTracks() {
		OwnerBean ownerBean = getOwnerBean();
		EncoderBean encoderBean = getEncoderDao().getAll().last();		
		SortedSet<EncodedTrackBean> encodedTrackBeans = 
			getOwnerService().getOwnedEncodedTracks(ownerBean, encoderBean);
		List<String> actual = new ArrayList<String>(encodedTrackBeans.size());
		Transformer<EncodedTrackBean, String> transformer = new Transformer<EncodedTrackBean, String>() {
			@Override
			public String transform(EncodedTrackBean encodedTrackBean) {
				return encodedTrackBean.getFlacUrl();
			}
		};
		CollectionUtils.collect(encodedTrackBeans, transformer, actual);
		
		String[] expected = {
			"music/Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage.ogg",
		  "music/Brutal Truth/Sounds of The Animal Kingdom Kill Trend Suicide/09 - Callous.ogg",
		  "music/Napalm Death/Scum/24 - Your Achievement_ (Bonus Track).ogg",
		  "music/Napalm Death/Scum/25 - Dead (Bonus Track).ogg"
		};		
		assertEquals("The wrong encoded tracks were returned.", Arrays.asList(expected), actual);
	}
	
	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		OwnerBean ownerBean = getOwnerBean();
		addOwnedAlbum(ownerBean, "SCUM", "NAPALM DEATH");
		addOwnedArtist(ownerBean, "BRUTAL TRUTH");
		getOwnerDao().store(ownerBean);
	}
	
	protected OwnerBean getOwnerBean() {
		return getOwnerDao().getAll().first();
	}
	
	protected void addOwnedAlbum(OwnerBean ownerBean, String albumIdentifier, String artistIdentifier) {
		
		EncodedArtistBean encodedArtistBean = 
			getEncodedArtistDao().findByIdentifier(artistIdentifier);
		EncodedAlbumBean encodedAlbumBean = 
			getEncodedAlbumDao().findByArtistAndIdentifier(encodedArtistBean, albumIdentifier);
		SortedSet<EncodedAlbumBean> encodedAlbumBeans = ownerBean.getEncodedAlbumBeans();
		if (encodedAlbumBeans == null) {
			encodedAlbumBeans = new TreeSet<EncodedAlbumBean>();
			ownerBean.setEncodedAlbumBeans(encodedAlbumBeans);
		}
		encodedAlbumBeans.add(encodedAlbumBean);
	}
	
	protected void addOwnedArtist(OwnerBean ownerBean, String artistIdentifier) {
		EncodedArtistBean encodedArtistBean = getEncodedArtistDao().findByIdentifier(artistIdentifier);
		SortedSet<EncodedArtistBean> encodedArtistBeans = ownerBean.getEncodedArtistBeans();
		if (encodedArtistBeans == null) {
			encodedArtistBeans = new TreeSet<EncodedArtistBean>();
			ownerBean.setEncodedArtistBeans(encodedArtistBeans);
		}
		encodedArtistBeans.add(encodedArtistBean);
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

}
