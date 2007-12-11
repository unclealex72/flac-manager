/**
 * 
 */
package uk.co.unclealex.music.image.amazon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import uk.co.unclealex.music.core.image.Album;
import uk.co.unclealex.music.core.image.SearchManager;

/**
 * @author alex
 *
 */
public class TestAmazonSearchManager implements SearchManager {

	public List<Album> search(String artist, String title) {
		File dir = new File("/home/alex/amazon-test/" + escape(artist) + "/" + escape(title) + "/covers");
		SortedMap<Integer, File> xmlFilesByIndex = new TreeMap<Integer, File>();
		addFiles(xmlFilesByIndex, "xml", dir);
		SortedMap<Integer, File> jpgFilesByIndex = new TreeMap<Integer, File>();
		addFiles(jpgFilesByIndex, "jpg", dir);
		
		List<Album> albums = new LinkedList<Album>();
		for (int idx : xmlFilesByIndex.keySet()) {
			try {
				StringWriter writer = new StringWriter();
				Reader reader = new FileReader(xmlFilesByIndex.get(idx)); 
				IOUtils.copy(reader, writer);
				reader.close();
				String xml = writer.toString();
				List<String> artists = extractElements(xml, "artist");
				String albumTitle = extractElements(xml, "title").get(0);
				File jpgFile = jpgFilesByIndex.get(idx);
				String imageUrl = "file://" + jpgFile.getAbsolutePath();
				List<String> tracks = extractElements(xml, "tracks");
				BufferedImage image = ImageIO.read(jpgFile);
				albums.add(new Album(artists, albumTitle, imageUrl, image.getHeight() * image.getWidth(), tracks));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return albums;
	}

	private List<String> extractElements(String xml, String elementName) {
		List<String> values = new LinkedList<String>();
		Pattern pattern = Pattern.compile("<" + elementName + ">(.*?)</" + elementName + ">");
		for (Matcher matcher = pattern.matcher(xml); matcher.find(); ) {
			values.add(matcher.group(1));
		}
		return values;
	}
	/**
	 * @param filesByIndex
	 * @param string
	 */
	private void addFiles(SortedMap<Integer, File> filesByIndex, String ext, File dir) {
		final String fullExt = "." + ext;
		File[] files = dir.listFiles(
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(fullExt);
					}
				});
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				int idx = new Integer(name.substring(5, 8));
				filesByIndex.put(idx, file);
			}
		}
	}

	private String escape(String s) {
		char[] result = new char[s.length()];
		for (int idx = 0; idx < s.length(); idx++) {
			char c = s.charAt(idx);
			result[idx] = Character.isLetterOrDigit(c) ? Character.toLowerCase(c) : '_'; 
		}
		return new String(result);
	}
}
