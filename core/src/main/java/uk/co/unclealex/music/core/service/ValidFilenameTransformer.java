package uk.co.unclealex.music.core.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

public class ValidFilenameTransformer implements Transformer<String, String> {

	private static final Map<Character, String> TRANSFORMS = new HashMap<Character, String>();
	static {
		TRANSFORMS.put('\u00B4', "'");
		TRANSFORMS.put('\u0060', "'");
		TRANSFORMS.put('"', "'");
		TRANSFORMS.put('/', " ");
		TRANSFORMS.put('\\', " ");
		TRANSFORMS.put('&', "and");
		TRANSFORMS.put('!', "");
		TRANSFORMS.put('<', "");
		TRANSFORMS.put('>', "");
		TRANSFORMS.put(':', "");
		TRANSFORMS.put(';', "");
		TRANSFORMS.put('^', "");
		TRANSFORMS.put('$', "");
		TRANSFORMS.put('?', "");
		TRANSFORMS.put('|', "");
		TRANSFORMS.put('*', "");
	}
	@Override
	public String transform(String input) {
		StringBuffer validFilename = new StringBuffer();
		for (char c : input.toCharArray()) {
			String replacement = TRANSFORMS.get(c);
			if (replacement != null) {
				validFilename.append(replacement);
			}
			else {
				validFilename.append(c);
			}
		}
		String filename = validFilename.toString().trim();
		while (filename.endsWith(".")) {
			filename = filename.substring(0, filename.length() - 1);
		}
		return filename.trim();
	}

}
