/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author alex
 *
 */
public class SlimServerConfig {

	private List<String> i_definiteArticles = new LinkedList<String>();
	private File i_baseDir;
	private boolean i_initialised = false;
	
	public void initialise(File configFile) throws IOException {
		initialise(new FileReader(configFile));
	}
	
	/**
	 * Read a slimserver configuration file. Currently, only single line properties are supported. 
	 * @param in
	 * @throws IOException
	 */
	public void initialise(Reader in) throws IOException {
		if (i_initialised) {
			throw new IllegalStateException("This configuration has already been initialised.");
		}
		i_initialised = true;
		Map<String, String> properties = new HashMap<String, String>();
		BufferedReader bIn = new BufferedReader(in);
		String line;
		while ((line = bIn.readLine()) != null) {
			int delimiterIndex = line.indexOf(':');
			if (
					delimiterIndex > 0 && delimiterIndex != line.length() - 1 && 
					!Character.isWhitespace(line.charAt(0))) {
				String key = line.substring(0, delimiterIndex).trim();
				String value = line.substring(delimiterIndex + 1).trim();
				if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
					value = value.substring(1, value.length() - 1);
				}
				properties.put(key.toLowerCase(), value);
			}
		}
		bIn.close();
		
		i_baseDir = new File(properties.get("audiodir"));
		i_definiteArticles = Arrays.asList(StringUtils.splitByWholeSeparator(properties.get("ignoredarticles"), null));
	}
	
	public File getBaseDir() {
		return i_baseDir;
	}
	/**
	 * @return the definiteArticles
	 */
	public List<String> getDefiniteArticles() {
		return i_definiteArticles;
	}
	/**
	 * @return the initialised
	 */
	public boolean isInitialised() {
		return i_initialised;
	}
	
	
}
