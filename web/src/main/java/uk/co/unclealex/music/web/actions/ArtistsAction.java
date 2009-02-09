package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;

import uk.co.unclealex.music.base.dao.FlacArtistDao;
import uk.co.unclealex.music.base.model.FlacArtistBean;

import com.opensymphony.xwork2.Preparable;

public class ArtistsAction extends BaseAction implements Preparable {

	private SortedSet<FlacArtistBean> i_flacArtistBeans;
	private FlacArtistDao i_flacArtistDao;
	
	@Override
	public void prepare() {
		setFlacArtistBeans(getFlacArtistDao().getAll());
	}

	public SortedSet<FlacArtistBean> getFlacArtistBeans() {
		return i_flacArtistBeans;
	}

	public void setFlacArtistBeans(SortedSet<FlacArtistBean> flacArtistBeans) {
		i_flacArtistBeans = flacArtistBeans;
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}
}
