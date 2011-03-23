package uk.co.unclealex.music;

public class Encoding implements Comparable<Encoding> {

	private String i_extension;
	
	public Encoding(String extension) {
		super();
		i_extension = extension;
	}

	@Override
	public int hashCode() {
		return getExtension().hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public int compareTo(Encoding o) {
		return getExtension().compareTo(o.getExtension());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Encoding && compareTo((Encoding) obj) == 0;
	}
	
	public String getExtension() {
		return i_extension;
	}
}
