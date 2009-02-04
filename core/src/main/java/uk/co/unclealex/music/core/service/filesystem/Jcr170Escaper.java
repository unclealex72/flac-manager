package uk.co.unclealex.music.core.service.filesystem;

import org.apache.jackrabbit.util.Text;

public final class Jcr170Escaper {

	protected static String[] NAMESPACES = new String[] { "jcr", "nt", "mix" };
	
	public static final String escape(String path) {
		String escapedPath = Text.escapeIllegalJcrChars(path);
		boolean namespaceNotFound = true;
		for (int idx = 0; namespaceNotFound && idx < NAMESPACES.length; idx++) {
			String namespace = NAMESPACES[idx];
			if (escapedPath.startsWith(namespace + "%3A")) {
				escapedPath = escapedPath.replaceFirst("%3A", ":");
				namespaceNotFound = false;
			}
		}
		return escapedPath.replaceAll("%2F", "/");
	}
	
	public static final String unescape(String path) {
		return Text.unescapeIllegalJcrChars(path);
	}

}
