package uk.co.unclealex.music;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

public class LatinServiceImpl implements LatinService {

	private Map<Character, String> i_latinMap = new HashMap<Character, String>();
	
	@PostConstruct
	protected void initialise() {
		add("A", "\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5");
		add("Ae", "\u00c6");
		add("C", "\u00c7");
		add("E", "\u00c8\u00c9\u00ca\u00cb");
		add("I", "\u00cc\u00cd\u00ce\u00cf");
		add("D", "\u00d0");
		add("N", "\u00d1");
		add("O", "\u00d2\u00d3\u00d4\u00d5\u00d6\u00d8");
		add("U", "\u00d9\u00da\u00db\u00dc");
		add("Y", "\u00dd");
		add("Ss", "\u00df");
	}

	protected void add(String ascii, String latins) {
		Map<Character, String> latinMap = getLatinMap();
		for (char latin : latins.toCharArray()) {
			latinMap.put(latin, ascii);
			latinMap.put(Character.toLowerCase(latin), ascii.toLowerCase());
		}
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.LatinService#removeCommonAccents(java.lang.String)
	 */
	public String removeCommonAccents(String str) {
		Map<Character, String> latinMap = getLatinMap();
		StringBuffer builder = new StringBuffer();
		for (char ch : str.toCharArray()) {
			String subs = latinMap.get(ch);
			if (subs != null) {
				builder.append(subs);
			}
			else {
				builder.append(ch);
			}
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		new LatinServiceImpl().initialise();
	}
	
	public Map<Character, String> getLatinMap() {
		return i_latinMap;
	}
}
