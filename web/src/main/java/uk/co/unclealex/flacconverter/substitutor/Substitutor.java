/**
 * 
 */
package uk.co.unclealex.flacconverter.substitutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatVariable;

/**
 * @author alex
 *
 */
public class Substitutor {

	private String i_text;
	
	/**
	 * @param text
	 * @param variableName
	 */
	public Substitutor(String text) {
		i_text = text;
	}

	public Substitutor substitute(TitleFormatVariable variable, final String value) {
		substitute(
				variable,
				new SubsBuilder() {
					public String getObjectAsString(Integer length) {
						if (length == null || length >= value.length()) {
							return value;
						}
						return value.substring(0, length);
					}
				});
		return this;
	}
	
	public Substitutor substitute(TitleFormatVariable variable, final int value) {
		substitute(
				variable,
				new SubsBuilder() {
					public String getObjectAsString(Integer length) {
						StringBuffer buf = new StringBuffer();
						buf.append(value);
						while (length != null && buf.length() < length) {
							buf.insert(0, '0');
						}
						return buf.toString();
					}
				});
		return this;
	}
	
	protected void substitute(TitleFormatVariable variable, SubsBuilder subsBuilder) {
		Pattern pattern = Pattern.compile("\\$\\{(?:([0-9]+):)?" + variable + "\\}");
		Matcher matcher;
		while ((matcher = pattern.matcher(getText())).find()) {
			Integer length = null;
			if (matcher.groupCount() > 0) {
				String sLength = matcher.group(1);
				if (sLength != null) {
					length = Integer.parseInt(sLength);
				}
			}
			setText(matcher.replaceFirst(subsBuilder.getObjectAsString(length)));
		}
	}
	
	private interface SubsBuilder {
		public String getObjectAsString(Integer length);
	}
	
	/**
	 * @param text the text to set
	 */
	protected void setText(String text) {
		i_text = text;
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return i_text;
	}
}
