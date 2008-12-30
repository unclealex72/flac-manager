package uk.co.unclealex.music.core.service;

import java.util.SortedSet;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;

@Service
@Transactional
public class FlacAlbumServiceImpl implements FlacAlbumService {

	private Pattern i_albumPathPattern;

	public FlacAlbumServiceImpl() {
		setAlbumPathPattern(Pattern.compile("[a-z]+?://(.+)"));
	}
	
	@Override
	public String getPathForFlacAlbum(FlacAlbumBean flacAlbumBean) {
		SortedSet<FlacTrackBean> flacTrackBeans = flacAlbumBean.getFlacTrackBeans();
		if (flacTrackBeans == null || flacTrackBeans.isEmpty()) {
			return null;
		}
		FlacTrackBean flacTrackBean = flacTrackBeans.first();
		return flacTrackBean.getFile().getParent();
	}

	public Pattern getAlbumPathPattern() {
		return i_albumPathPattern;
	}

	public void setAlbumPathPattern(Pattern albumPathPattern) {
		i_albumPathPattern = albumPathPattern;
	}

}
