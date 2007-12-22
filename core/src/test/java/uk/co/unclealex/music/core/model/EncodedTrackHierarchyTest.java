package uk.co.unclealex.music.core.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.music.core.CoreSpringTest;

public class EncodedTrackHierarchyTest extends CoreSpringTest {

	public void testHierarchy() {
		Map<String, Map<String, List<String>>> hierarchy = new HashMap<String, Map<String,List<String>>>();
		for (String path : TRACKS) {
			String[] parts = StringUtils.split(path, '/');
			String artist = parts[0], album = parts[1], track = parts[2].substring(5);
			Map<String, List<String>> albumHierarchy = hierarchy.get(artist);
			if (albumHierarchy == null) {
				albumHierarchy = new HashMap<String, List<String>>();
				hierarchy.put(artist, albumHierarchy);
			}
			List<String> tracks = albumHierarchy.get(album);
			if (tracks == null) {
				tracks = new LinkedList<String>();
				albumHierarchy.put(album, tracks);
			}
			tracks.add(track);
		}
		Transformer<EncodedArtistBean, String> artistTransformer = new Transformer<EncodedArtistBean, String>() {
			@Override
			public String transform(EncodedArtistBean encodedArtistBean) {
				return encodedArtistBean.getName();
			}
		};
		Transformer<EncodedAlbumBean, String> albumTransformer = new Transformer<EncodedAlbumBean, String>() {
			@Override
			public String transform(EncodedAlbumBean encodedAlbumBean) {
				return encodedAlbumBean.getTitle();
			}
		};
		Transformer<EncodedTrackBean, String> trackTransformer = new Transformer<EncodedTrackBean, String>() {
			@Override
			public String transform(EncodedTrackBean encodedTrackBean) {
				return encodedTrackBean.getTitle();
			}
		};
		SortedSet<EncodedArtistBean> encodedArtistBeans = getEncodedArtistDao().getAll();
		assertEquals(
			"The wrong artist names were returned.", 
			hierarchy.keySet(), CollectionUtils.collect(encodedArtistBeans, artistTransformer));
		for (EncodedArtistBean encodedArtistBean : encodedArtistBeans) {
			String artist = artistTransformer.transform(encodedArtistBean);
			Map<String, List<String>> albumHierarchy = hierarchy.get(artist);
			SortedSet<EncodedAlbumBean> encodedAlbumBeans = encodedArtistBean.getEncodedAlbumBeans();
			assertEquals(
					"The wrong albums were returned for " + artist,
					albumHierarchy.keySet(), CollectionUtils.collect(encodedAlbumBeans, albumTransformer));
			for (EncodedAlbumBean encodedAlbumBean : encodedAlbumBeans) {
				SortedSet<String> tracks = new TreeSet<String>();
				String album = albumTransformer.transform(encodedAlbumBean);
				CollectionUtils.collect(encodedAlbumBean.getEncodedTrackBeans(), trackTransformer, tracks);
				assertEquals("The wrong tracks were returned for " + album, albumHierarchy.get(album), tracks);
			}
		}
	}
}
