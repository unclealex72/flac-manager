package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;

import org.apache.log4j.Logger;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

import com.opensymphony.xwork2.ActionSupport;

public abstract class AbstractAlbumCoverAction extends ActionSupport {

	private Logger log = Logger.getLogger(getClass());
	
	private AlbumCoverBean i_albumCoverBean;
	private FlacAlbumBean i_flacAlbumBean;
	private AlbumCoverService i_albumCoverService;
	private SortedSet<AlbumCoverBean> i_albumCoverBeans;

	@Override
	public String execute() {
		try {
			doExecute();
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e);
			populateCovers();
			return INPUT;
		}
	}
	
	public abstract void doExecute() throws Exception;
	
	public void fail(Exception e) {
		// Add any errors here.
	}
	
	protected void populateCovers() {
		setAlbumCoverBeans(getAlbumCoverService().findCoversForAlbum(getFlacAlbumBean()));
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}

	public AlbumCoverBean getAlbumCoverBean() {
		return i_albumCoverBean;
	}

	public void setAlbumCoverBean(AlbumCoverBean albumCoverBean) {
		i_albumCoverBean = albumCoverBean;
	}

	public FlacAlbumBean getFlacAlbumBean() {
		return i_flacAlbumBean;
	}

	public void setFlacAlbumBean(FlacAlbumBean flacAlbumBean) {
		i_flacAlbumBean = flacAlbumBean;
	}

	public SortedSet<AlbumCoverBean> getAlbumCoverBeans() {
		return i_albumCoverBeans;
	}

	public void setAlbumCoverBeans(SortedSet<AlbumCoverBean> albumCoverBeans) {
		i_albumCoverBeans = albumCoverBeans;
	}
}
