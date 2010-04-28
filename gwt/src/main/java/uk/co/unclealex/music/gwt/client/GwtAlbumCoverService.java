package uk.co.unclealex.music.gwt.client;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

import uk.co.unclealex.music.gwt.client.action.Action;
import uk.co.unclealex.music.gwt.client.action.ActionFactory;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("albumCoverService")
public interface GwtAlbumCoverService extends RemoteService {
	
	public SortedMap<AlbumInformationBean, String> listMissingCovers(ActionFactory<Object> actionFactory) throws IOException;
	
	public SortedMap<ArtistInformationBean, String> listArtists(char firstLetter, ActionFactory<ArtistInformationBean> actionFactory) throws IOException;
	
	public SortedMap<AlbumInformationBean, String> listAlbums(ArtistInformationBean artistInformationBean, ActionFactory<AlbumInformationBean> actionFactory) throws IOException;

	public String createArtworkLink(String relativePath);
	
	public SortedMap<Character, String> listFirstLetters(ActionFactory<Character> actionFactory) throws IOException;

	public List<String> searchForArtwork(String artist, String album) throws IOException;
	
	public String serialise(Action action) throws IOException;

	public Action deserialise(String token) throws IOException;	
}