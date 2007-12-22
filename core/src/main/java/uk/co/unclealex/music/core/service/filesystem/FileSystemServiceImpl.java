package uk.co.unclealex.music.core.service.filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

@Service
@Transactional
public class FileSystemServiceImpl implements FileSystemService {

	private static final char SLASH = '/';
	private PathComponentFactory i_pathComponentFactory;

	@Override
	public EncodedTrackBean findByPath(String path) throws PathNotFoundException {
		if (!path.isEmpty() && path.charAt(path.length() - 1) == SLASH) {
			throw new PathNotFoundException(path);
		}
		EncodedTrackBean encodedTrackBean = createContext(path).getEncodedTrackBean();
		if (encodedTrackBean == null) {
			throw new PathNotFoundException(path);
		}
		return encodedTrackBean;
	}

	@Override
	public SortedSet<String> getChildren(String directory) throws PathNotFoundException {
		return createContext(directory).getChildren();
	}
	
	@Override
	public boolean isDirectory(String path) throws PathNotFoundException {
		return getChildren(path) != null;
	}

	protected Context createContext(String path) throws PathNotFoundException {
		PathComponentFactory pathComponentFactory = getPathComponentFactory();
		Context context = new Context();
		List<PathComponent> pathComponents = new ArrayList<PathComponent>(6);
		pathComponents.add(pathComponentFactory.createRootPathComponent(context));
		pathComponents.add(pathComponentFactory.createEncoderPathComponent(context));
		pathComponents.add(pathComponentFactory.createFirstLetterOfArtistPathComponent(context));
		pathComponents.add(pathComponentFactory.createArtistPathComponent(context));
		pathComponents.add(pathComponentFactory.createAlbumPathComponent(context));
		pathComponents.add(pathComponentFactory.createTrackPathComponent(context));
		
		Iterator<PathComponent> pathComponentIterator = pathComponents.iterator();
		Iterator<String> pathIterator = Arrays.asList(StringUtils.split(path, SLASH)).iterator();
		PathComponent finalPathComponent = null;
		PathComponent pathComponent = null;
		while (
			pathComponentIterator.hasNext() && 
			(!((pathComponent = pathComponentIterator.next()) instanceof VisiblePathComponent) || pathIterator.hasNext())) {
			finalPathComponent = pathComponent;
			if (pathComponent instanceof VisiblePathComponent) {
				((VisiblePathComponent) pathComponent).setPathComponent(pathIterator.next());
			}
		}
		if (pathIterator.hasNext()) {
			// Too many paths!
			throw new PathNotFoundException(StringUtils.join(pathIterator, SLASH));
		}
		context.setChildren(finalPathComponent.getChildren());
		return context;
	}

	public PathComponentFactory getPathComponentFactory() {
		return i_pathComponentFactory;
	}

	@Required
	public void setPathComponentFactory(PathComponentFactory pathComponentFactory) {
		i_pathComponentFactory = pathComponentFactory;
	}
}
