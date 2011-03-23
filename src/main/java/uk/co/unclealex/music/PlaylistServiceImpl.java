package uk.co.unclealex.music;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;

import uk.co.unclealex.music.inject.PlaylistsDirectory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class PlaylistServiceImpl implements PlaylistService {

	private File i_playlistsDirectory;
	private FileService i_fileService;
	
	@Inject
	protected PlaylistServiceImpl(@PlaylistsDirectory File playlistsDirectory, FileService fileService) {
		super();
		i_playlistsDirectory = playlistsDirectory;
		i_fileService = fileService;
	}


	@Override
	public Map<String, Iterable<String>> createPlaylists(String owner, final Encoding encoding) {
		File[] playlistFiles = 
			getPlaylistsDirectory().listFiles(
				(FilenameFilter) FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".m3u", IOCase.INSENSITIVE)));
		Map<String, List<File>> ownedFlacFilesByPlaylistName =
			Maps.transformValues(Maps.uniqueIndex(Arrays.asList(playlistFiles), baseNameFunction()), Functions.compose(ownedFilesOnlyFunction(owner), playlistParsingFunction()));
		Map<String, List<File>> nonEmptyOwnedFlacFilesByPlaylistName = Maps.filterValues(ownedFlacFilesByPlaylistName, Predicates.not(emptyPredicate()));
		Function<List<File>, Iterable<String>> relativePathsFunction = new Function<List<File>, Iterable<String>>() {
			@Override
			public Iterable<String> apply(List<File> flacFiles) {
				return Iterables.transform(flacFiles, relativePathFunction(encoding));
			}
		};
		return Maps.transformValues(nonEmptyOwnedFlacFilesByPlaylistName, relativePathsFunction);
	}

	protected Function<File, String> relativePathFunction(final Encoding encoding) {
		final FileService fileService = getFileService();
		return new Function<File, String>() {
			@Override
			public String apply(File flacFile) {
				return fileService.relativiseEncodedFileKeepingFirstLetter(fileService.translateFlacFileToEncodedFile(flacFile, encoding));
			}
		};
	}
	
	protected Function<File, List<File>> playlistParsingFunction() {
		final Predicate<String> isFlacLinePredicate = new Predicate<String>() {
			@Override
			public boolean apply(String line) {
				return !line.startsWith("#") && line.endsWith(".flac");
			}
		};
		final Function<String, String> trimFunction = new Function<String, String>() {
			@Override
			public String apply(String str) {
				return str.trim();
			}
		};
		final Function<String, File> pathFunction = new Function<String, File>() {
			@Override
			public File apply(String path) {
				return new File(path);
			}
		};
		return new Function<File, List<File>>() {
			@Override
			public List<File> apply(File m3uFile) {
				try {
					return Lists.newArrayList(
						Iterables.transform(
							Iterables.filter(
								Iterables.transform(
									Files.readLines(m3uFile, Charset.defaultCharset()),
									trimFunction),
								isFlacLinePredicate),
							pathFunction));
				}
				catch (IOException e) {
					return Collections.emptyList();
				}
			}
		};
	}


	protected Predicate<Collection<?>> emptyPredicate() {
		return new Predicate<Collection<?>>() {
			@Override
			public boolean apply(Collection<?> collection) {
				return collection.isEmpty();
			}
		};
	}


	protected Function<List<File>, List<File>> ownedFilesOnlyFunction(final String owner) {
		final FileService fileService = getFileService();
		final Predicate<File> isOwnedPredicate = new Predicate<File>() {
			@Override
			public boolean apply(File flacFile) {
				return fileService.isFlacFileIsOwnedBy(flacFile, owner);
			}
		};
		return new Function<List<File>, List<File>>() {
			@Override
			public List<File> apply(List<File> flacFiles) {
				return Lists.newArrayList(Iterables.filter(flacFiles, isOwnedPredicate));
			}
		};
	}


	protected Function<File, String> baseNameFunction() {
		return new Function<File, String>() {
			@Override
			public String apply(File file) {
				return FilenameUtils.getBaseName(file.getPath());
			}
		};
	}


	public File getPlaylistsDirectory() {
		return i_playlistsDirectory;
	}


	public FileService getFileService() {
		return i_fileService;
	}
}
