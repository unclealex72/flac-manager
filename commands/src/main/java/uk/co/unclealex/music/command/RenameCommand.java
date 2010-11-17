package uk.co.unclealex.music.command;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.encoding.RenamingService;

public class RenameCommand extends SpringCommand implements Transformer<String, File> {

	private static final String ALBUM_OPTION = "album";
	private static final String ARTIST_OPTION = "artist";
	private static final String TRACK_NUMBER_OPTION = "track";
	private static final String TITLE_OPTION = "title";
	private static final String FILES_OPTION = "files";
	
	private Set<File> i_flacFiles;
	private String i_artist;
	private String i_album;
	private Integer i_trackNumber;
	private String i_title;
	
	public static void main(String[] args) {
		new RenameCommand().run(args);
	}

	@Override
	public File transform(String path) {
		return new File(path);
	}
	
	@SuppressWarnings("static-access")
	@Override
	protected Option[] addOptions() {
		Option artistOption =
			OptionBuilder.
				hasArg().
				withArgName("artist name").
				withDescription("The new artist name.").
				create(ARTIST_OPTION);
		Option albumOption = 
			OptionBuilder.
				hasArg().
				withArgName("album name").
				withDescription("The new album name.").
				create(ALBUM_OPTION);
		Option trackNumberOption = 
			OptionBuilder.
				hasArg().
				withArgName("track number").
				withDescription("The new track number.").
				create(TRACK_NUMBER_OPTION);
		Option titleOption = 
				OptionBuilder.
					hasArg().
					withArgName("title").
					withDescription("The new track title.").
					create(TITLE_OPTION);
		Option filesOption = 
			OptionBuilder.
				hasArgs().
				withArgName("files").
				withDescription("The files to alter.").
				isRequired().
				create(FILES_OPTION);
		return new Option[] { artistOption, albumOption, trackNumberOption, titleOption, filesOption };
	}
	
	@Override
	protected void checkCommandLine(CommandLine commandLine) throws ParseException {
		Set<File> flacFiles = CollectionUtils.collect(Arrays.asList(commandLine.getOptionValues(FILES_OPTION)), this, new LinkedHashSet<File>());
		if (flacFiles.isEmpty()) {
			throw new ParseException("You must supply at least one flac file.");
		}
		String artist = commandLine.getOptionValue(ARTIST_OPTION);
		String album = commandLine.getOptionValue(ALBUM_OPTION);
		String trackNumber = commandLine.getOptionValue(TRACK_NUMBER_OPTION);
		String title = commandLine.getOptionValue(TITLE_OPTION);
		Integer trackNo = null;
		if (trackNumber != null) {
			try {
				trackNo = Integer.valueOf(trackNumber);
			}
			catch (NumberFormatException e) {
				throw new ParseException("A supplied track number must be numeric.");
			}
		}
		if (artist == null && album == null && trackNo == null && title == null) {
			throw new ParseException(
				String.format(
					"At least one of %s, %s, %s or %s must be supplied.", 
					ARTIST_OPTION, ALBUM_OPTION, TRACK_NUMBER_OPTION, TITLE_OPTION));
		}
		if ((trackNo != null || title != null) && flacFiles.size() != 1) {
			throw new ParseException(
				String.format(
					"If either of the %s or %s options are selected there can be only one flac file.", TRACK_NUMBER_OPTION, TITLE_OPTION));
		}
		setArtist(artist);
		setAlbum(album);
		setTrackNumber(trackNo);
		setTitle(title);
		setFlacFiles(flacFiles);
	}
	
	@Override
	public void run(ApplicationContext ctxt, CommandLine commandLine) throws IOException {
		RenamingService renamingService = ctxt.getBean(RenamingService.class);
		renamingService.rename(getFlacFiles(), getArtist(), getAlbum(), getTrackNumber(), getTitle());
	}

	public Set<File> getFlacFiles() {
		return i_flacFiles;
	}

	public void setFlacFiles(Set<File> flacFiles) {
		i_flacFiles = flacFiles;
	}

	public String getArtist() {
		return i_artist;
	}

	public void setArtist(String artist) {
		i_artist = artist;
	}

	public String getAlbum() {
		return i_album;
	}

	public void setAlbum(String album) {
		i_album = album;
	}

	public Integer getTrackNumber() {
		return i_trackNumber;
	}

	public void setTrackNumber(Integer trackNumber) {
		i_trackNumber = trackNumber;
	}

	public String getTitle() {
		return i_title;
	}

	public void setTitle(String title) {
		i_title = title;
	}
}
