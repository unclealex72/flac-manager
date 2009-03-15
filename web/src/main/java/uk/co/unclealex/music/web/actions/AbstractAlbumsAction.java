package uk.co.unclealex.music.web.actions;

import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.web.model.FlacAlbumWithCoverBean;

import com.opensymphony.xwork2.Preparable;

public abstract class AbstractAlbumsAction extends BaseAction implements Preparable, AlbumsAction {

	private AlbumCoverService i_albumCoverService;
	
	private SortedMap<FlacArtistBean, SortedSet<FlacAlbumWithCoverBean>> i_albumBeansWithSelectedCoverByArtistBean;
	
	@Override
	public void prepare() {
		SortedMap<FlacArtistBean, SortedSet<FlacAlbumWithCoverBean>> albumBeansWithSelectedCoverByArtistBean = 
			new TreeMap<FlacArtistBean, SortedSet<FlacAlbumWithCoverBean>>();
		boolean displaySelectedCovers = displaySelectedCovers();
		Collection<FlacAlbumBean> albumsToDisplay = getAlbumsToDisplay();
		for (FlacAlbumBean flacAlbumBean : albumsToDisplay) {
			FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
			SortedSet<FlacAlbumWithCoverBean> albumWithCoverBeans = albumBeansWithSelectedCoverByArtistBean.get(flacArtistBean);
			if (albumWithCoverBeans == null) {
				albumWithCoverBeans = new TreeSet<FlacAlbumWithCoverBean>();
				albumBeansWithSelectedCoverByArtistBean.put(flacArtistBean, albumWithCoverBeans);
			}
			AlbumCoverBean albumCoverBean = null;
			if (displaySelectedCovers) {
				albumCoverBean = getAlbumCoverService().findSelectedCoverForFlacAlbum(flacAlbumBean);
			}
			albumWithCoverBeans.add(new FlacAlbumWithCoverBean(flacAlbumBean, albumCoverBean));
		}
		setAlbumBeansWithSelectedCoverByArtistBean(albumBeansWithSelectedCoverByArtistBean);
	}
	
	public abstract Collection<FlacAlbumBean> getAlbumsToDisplay();

	public abstract boolean displaySelectedCovers();
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.AlbumsAction#getAlbumBeansWithSelectedCoverByArtistBean()
	 */
	public SortedMap<FlacArtistBean, SortedSet<FlacAlbumWithCoverBean>> getAlbumBeansWithSelectedCoverByArtistBean() {
		return i_albumBeansWithSelectedCoverByArtistBean;
	}

	public void setAlbumBeansWithSelectedCoverByArtistBean(
			SortedMap<FlacArtistBean, SortedSet<FlacAlbumWithCoverBean>> albumBeansWithSelectedCoverByArtistBean) {
		i_albumBeansWithSelectedCoverByArtistBean = albumBeansWithSelectedCoverByArtistBean;
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}
}
