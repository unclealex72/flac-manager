package uk.co.unclealex.flacconverter.flac.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import uk.co.unclealex.music.encoder.flac.model.AbstractFlacBean;
import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public class TestFlacProvider {

	public static final String MADE_UP_URL =
		"file:///mnt/multimedia/flac/napalm_death/from_enslavement_to_obliteration/19_point_of_no_return.flac";
	
	private SortedSet<FlacTrackBean> i_allFlacTrackBeans = new TreeSet<FlacTrackBean>();
	private SortedSet<FlacAlbumBean> i_allFlacAlbumBeans = new TreeSet<FlacAlbumBean>();
	private SortedSet<FlacArtistBean> i_allFlacArtistBeans = new TreeSet<FlacArtistBean>();
	private int i_index;
	
	public void initialise() {
		String[] urls = new String[] {
				"file:///mnt/multimedia/flac/napalm_death/from_enslavement_to_obliteration/12_you_suffer.flac",
				MADE_UP_URL,
				"file:///mnt/multimedia/flac/napalm_death/scum/24_your_achievement_bonus_track.flac",
				"file:///mnt/multimedia/flac/napalm_death/scum/25_dead_bonus_track.flac",
				"file:///mnt/multimedia/flac/sod/speak_english_or_die/20_the_ballad_of_jimi_hendrix.flac",
				"file:///mnt/multimedia/flac/sod/speak_english_or_die/21_diamonds_and_rust_extended_version.flac",
				"file:///mnt/multimedia/flac/brutal_truth/sounds_of_the_animal_kingdomkill_trend_suicide/09_callous.flac",
				"file:///mnt/multimedia/flac/brutal_truth/extreme_conditions_demand_extreme_responses/07_collateral_damage.flac"
		};
		for (String url : urls) {
			createTrack(url);
		}
	}
	
	protected FlacTrackBean createTrack(String url) {
		FlacTrackBean flacTrackBean = new FlacTrackBean();
		List<String> parts = new ArrayList<String>();
		parts.addAll(Arrays.asList(StringUtils.split(url, File.separatorChar)));
		Collections.reverse(parts);
		
		String fileName = extractPart(parts, 0);
		String albumName = extractPart(parts, 1);
		String artistName = extractPart(parts, 2);
		
		int lastCharacter = fileName.lastIndexOf('.');
		int endOfNumber = fileName.indexOf(' ');
		String title = fileName.substring(endOfNumber + 1, lastCharacter);
		String trackNumber = fileName.substring(0, endOfNumber);
		
		flacTrackBean.setCode(fileName.toUpperCase());
		flacTrackBean.setId(nextIndex());
		flacTrackBean.setTimestamp(new Date().getTime());
		flacTrackBean.setRawTitle(title.getBytes());
		flacTrackBean.setTrackNumber(new Integer(trackNumber));
		flacTrackBean.setType("flc");
		flacTrackBean.setUrl(url);
		FlacAlbumBean flacAlbumBean = createAlbum(albumName, artistName);
		flacTrackBean.setFlacAlbumBean(flacAlbumBean);
		flacAlbumBean.getFlacTrackBeans().add(flacTrackBean);
		getAllFlacTrackBeans().add(flacTrackBean);
		return flacTrackBean;
	}
	
	protected FlacAlbumBean createAlbum(String albumName, String artistName) {
		FlacArtistBean flacArtistBean = createArtist(artistName);
		String code = albumName.toUpperCase();
		FlacAlbumBean flacAlbumBean =
			CollectionUtils.find(flacArtistBean.getFlacAlbumBeans(), getCodedPredicate(code));
		if (flacAlbumBean != null) {
			return flacAlbumBean;
		}
		flacAlbumBean = new FlacAlbumBean();
		flacAlbumBean.setCode(code);
		flacAlbumBean.setFlacArtistBean(flacArtistBean);
		flacAlbumBean.setFlacTrackBeans(new TreeSet<FlacTrackBean>());
		flacAlbumBean.setId(nextIndex());
		flacAlbumBean.setRawTitle(albumName.getBytes());
		flacArtistBean.getFlacAlbumBeans().add(flacAlbumBean);
		getAllFlacAlbumBeans().add(flacAlbumBean);
		return flacAlbumBean;
	}
	
	protected FlacArtistBean createArtist(String artistName) {
		String code = artistName.toUpperCase();		
		FlacArtistBean flacArtistBean =
			CollectionUtils.find(getAllFlacArtistBeans(), getCodedPredicate(code));
		if (flacArtistBean != null) {
			return flacArtistBean;
		}
		flacArtistBean = new FlacArtistBean();
		flacArtistBean.setCode(code);
		flacArtistBean.setFlacAlbumBeans(new TreeSet<FlacAlbumBean>());
		flacArtistBean.setId(nextIndex());
		flacArtistBean.setRawName(artistName.getBytes());
		getAllFlacArtistBeans().add(flacArtistBean);
		return flacArtistBean;
	}
	
	protected String extractPart(List<String> parts, int index) {
		return WordUtils.capitalizeFully(parts.get(index).replace('_', ' '));
	}
	
	public Predicate<AbstractFlacBean<?>> getCodedPredicate(final String code) {
		return new Predicate<AbstractFlacBean<?>>() {
			@Override
			public boolean evaluate(AbstractFlacBean<?> codedBean) {
				return codedBean.getCode().equals(code);
			}
		};
	}
	
	public int nextIndex() {
		return ++i_index;
	}
	
	public SortedSet<FlacTrackBean> getAllFlacTrackBeans() {
		return i_allFlacTrackBeans;
	}
	
	public void setAllFlacTrackBeans(SortedSet<FlacTrackBean> allFlacTrackBeans) {
		i_allFlacTrackBeans = allFlacTrackBeans;
	}
	
	public SortedSet<FlacAlbumBean> getAllFlacAlbumBeans() {
		return i_allFlacAlbumBeans;
	}
	
	public void setAllFlacAlbumBeans(SortedSet<FlacAlbumBean> allFlacAlbumBeans) {
		i_allFlacAlbumBeans = allFlacAlbumBeans;
	}
	
	public SortedSet<FlacArtistBean> getAllFlacArtistBeans() {
		return i_allFlacArtistBeans;
	}
	
	public void setAllFlacArtistBeans(SortedSet<FlacArtistBean> allFlacArtistBeans) {
		i_allFlacArtistBeans = allFlacArtistBeans;
	}
}
