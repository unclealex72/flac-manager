import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;


public class CoverAdder {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File workingDir = new File(new File(System.getProperty("user.home")), "queen");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return Arrays.asList(new String[] { "flac", "mp3", "ogg" }).contains(FilenameUtils.getExtension(name));
			}
		};
		File coveredDirectory = new File(workingDir, "covered");
		for (File file : workingDir.listFiles(filter)) {
			File destination = new File(coveredDirectory, file.getName());
			FileInputStream in = new FileInputStream(file);
			FileOutputStream out = new FileOutputStream(destination);
			in.getChannel().transferTo(0, file.length(), out.getChannel());
			in.close();
			out.close();
			AudioFile audioFile = AudioFileIO.read(destination);
			Tag tag = audioFile.getTag();
			tag.deleteArtworkField();
			File artworkFile = new File(workingDir, FilenameUtils.getBaseName(file.getName()) + ".jpg");
			Artwork artwork = Artwork.createArtworkFromFile(artworkFile);
			artwork.setDescription("");
			tag.createAndSetArtworkField(artwork);
			audioFile.commit();
		}
	}

}
