/**
 * 
 */
package uk.co.unclealex.music.core.substitutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.music.core.service.titleformat.TitleFormatVariable;

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

	public Substitutor substitute(TitleFormatVariable variable, String value) {
		substitute(
				variable,
				new SubsBuilder<String>() {
					public String getObjectAsString(String value, Integer length) {
						if (length == null || length >= value.length()) {
							return value;
						}
						return value.substring(0, length).replace('/', '_');
					}
				},
				value);
		return this;
	}
	
	public Substitutor substitute(TitleFormatVariable variable, Integer value) {
		substitute(
				variable,
				new SubsBuilder<Integer>() {
					public String getObjectAsString(Integer value, Integer length) {
						StringBuffer buf = new StringBuffer();
						buf.append(value.intValue());
						while (length != null && buf.length() < length) {
							buf.insert(0, '0');
						}
						return buf.toString();
					}
				},
				value);
		return this;
	}
	
	protected <T> void substitute(TitleFormatVariable variable, SubsBuilder<T> subsBuilder, T value) {
		Pattern pattern = createPattern(variable);
		Matcher matcher;
		while ((matcher = pattern.matcher(getText())).find()) {
			Integer length = null;
			if (matcher.groupCount() > 0) {
				String sLength = matcher.group(1);
				if (sLength != null) {
					length = Integer.parseInt(sLength);
				}
			}
			String substitution;
			if (value == null) {
				substitution = length==null?"%":StringUtils.repeat("_", length);
			}
			else {
				substitution = subsBuilder.getObjectAsString(value, length);
			}
			setText(matcher.replaceFirst(substitution));
		}
	}
	
	protected Pattern createPattern(TitleFormatVariable variable) {
		return Pattern.compile("\\$\\{(?:([0-9]+):)?" + variable + "\\}");
	}
	
	public boolean isTitleFormatVariableRequired(TitleFormatVariable variable) {
		return createPattern(variable).matcher(getText()).find();
	}
	
	private interface SubsBuilder<T> {
		public String getObjectAsString(T value, Integer length);
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
