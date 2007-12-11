package uk.co.unclealex.flacconverter.encoded.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.flacconverter.EncodedSpringTest;
import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.dao.OwnerDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class OwnerServiceTest extends EncodedSpringTest {

	private OwnerDao i_ownerDao;
	private OwnerService i_ownerService;
	private EncoderDao i_encoderDao;
	private FlacTrackDao i_flacTrackDao;
	private EncodedTrackDao i_encodedTrackDao;
	
	public void testOwnedAlbums() {
		OwnerBean ownerBean = getOwnerBean();
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

	public void testOwnedEncodedTracks() {
		OwnerBean ownerBean = getOwnerBean();
		EncoderBean encoderBean = getEncoderDao().getAll().last();
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		for (FlacTrackBean flacTrackBean : getFlacTrackDao().getAll()) {
			EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
			encodedTrackBean.setEncoderBean(encoderBean);
			encodedTrackBean.setFlacUrl(flacTrackBean.getUrl());
			encodedTrackBean.setTimestamp(System.currentTimeMillis());
			encodedTrackBean.setLength(0);
			encodedTrackDao.store(encodedTrackBean);
		}
		
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
				"file:///mnt/multimedia/flac/brutal_truth/extreme_conditions_demand_extreme_responses/07_collateral_damage.flac", 
				"file:///mnt/multimedia/flac/brutal_truth/sounds_of_the_animal_kingdomkill_trend_suicide/09_callous.flac", 
				"file:///mnt/multimedia/flac/napalm_death/scum/24_your_achievement_bonus_track.flac", 
				"file:///mnt/multimedia/flac/napalm_death/scum/25_dead_bonus_track.flac"
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

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
