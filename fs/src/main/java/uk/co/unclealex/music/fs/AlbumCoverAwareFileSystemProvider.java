package uk.co.unclealex.music.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.SimpleDataFileBean;

public abstract class AlbumCoverAwareFileSystemProvider implements FileSystemProvider {

	protected static final String COVER_FILENAME = "cover.png";
	private static final Pattern COVER_PATTERN = Pattern.compile("(.*)/" + COVER_FILENAME);

	@Override
	public FileBean findByPath(String path) throws IOException {
		Matcher matcher = COVER_PATTERN.matcher(path);
		FileBean result;
		if (matcher.matches()) {
			result = createAlbumCoverFileBean(path, findAlbumCoverByPath(matcher.group(1), path));
		}
		else {
			result = findNonAlbumCoverByPath(path);
		}
		if (result == null) {
			throw createFileNotFoundException(path);
		}
		return result;
	}

	protected FileNotFoundException createFileNotFoundException(String path) {
		return new FileNotFoundException(path);
	}

	protected SimpleDataFileBean createAlbumCoverFileBean(String path, AlbumCoverBean albumCoverBean) {
		return new SimpleDataFileBean(path, albumCoverBean.getCoverDataBean());
	}

	protected abstract FileBean findNonAlbumCoverByPath(String path) throws IOException;
	
	protected abstract AlbumCoverBean findAlbumCoverByPath(String directory, String path) throws IOException;
}
