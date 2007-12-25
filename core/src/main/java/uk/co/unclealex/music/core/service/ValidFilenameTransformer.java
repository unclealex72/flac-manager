package uk.co.unclealex.music.core.service;

import org.apache.commons.collections15.Transformer;

public class ValidFilenameTransformer implements Transformer<String, String> {

	private static String INVALID_CHARACTERS = "!/\\<>:;?^$\"";
	@Override
	public String transform(String input) {
		StringBuffer validFilename = new StringBuffer();
		for (char c : input.toCharArray()) {
			if (c == '&') {
				validFilename.append("and");
			}
			else if (isValidCharacter(c)) {
				validFilename.append(c);
			}
		}
		return validFilename.toString();
	}

	protected boolean isValidCharacter(char c) {
		return c >= ' ' && INVALID_CHARACTERS.indexOf(c) == -1;
	}
}
