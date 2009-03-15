package uk.co.unclealex.music.web.actions;

import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.web.model.FlacAlbumWithCoverBean;

public interface AlbumsAction {

	public SortedMap<FlacArtistBean, SortedSet<FlacAlbumWithCoverBean>> getAlbumBeansWithSelectedCoverByArtistBean();

}