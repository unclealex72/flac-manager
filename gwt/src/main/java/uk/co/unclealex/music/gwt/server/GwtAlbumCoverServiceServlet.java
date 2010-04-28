package uk.co.unclealex.music.gwt.server;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.music.gwt.client.GwtAlbumCoverService;
import uk.co.unclealex.music.gwt.client.action.Action;
import uk.co.unclealex.music.gwt.client.action.ActionFactory;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class GwtAlbumCoverServiceServlet extends RemoteServiceServlet implements
    GwtAlbumCoverService {

	private GwtAlbumCoverService i_delegate;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		WebApplicationContext ctxt = 
			WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		GwtAlbumCoverService gwtAlbumCoverService = ctxt.getBean("gwtAlbumCoverService", GwtAlbumCoverService.class);
		setDelegate(gwtAlbumCoverService);
		super.init(config);
	}
	
	public SortedMap<AlbumInformationBean, String> listMissingCovers(ActionFactory<Object> actionFactory) throws IOException {
		return getDelegate().listMissingCovers(actionFactory);
	}

	public SortedMap<AlbumInformationBean, String> listAlbums(ArtistInformationBean artistInformationBean, ActionFactory<AlbumInformationBean> actionFactory) throws IOException {
		return getDelegate().listAlbums(artistInformationBean, actionFactory);
	}

	public SortedMap<ArtistInformationBean, String> listArtists(char firstLetter, ActionFactory<ArtistInformationBean> actionFactory) throws IOException {
		return getDelegate().listArtists(firstLetter, actionFactory);
	}

	public SortedMap<Character, String> listFirstLetters(ActionFactory<Character> actionFactory) throws IOException {
		return getDelegate().listFirstLetters(actionFactory);
	}

	public String createArtworkLink(String relativePath) {
		return getDelegate().createArtworkLink(relativePath);
	}
	
	public String serialise(Action action) throws IOException {
		return getDelegate().serialise(action);
	}
	
	public Action deserialise(String token) throws IOException {
		return getDelegate().deserialise(token);
	}

	public List<String> searchForArtwork(String artist, String album) throws IOException {
		return getDelegate().searchForArtwork(artist, album);
	}
	
	public GwtAlbumCoverService getDelegate() {
		return i_delegate;
	}

	public void setDelegate(GwtAlbumCoverService delegate) {
		i_delegate = delegate;
	}

}