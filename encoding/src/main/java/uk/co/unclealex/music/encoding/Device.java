package uk.co.unclealex.music.encoding;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Device implements Comparable<Device> {

	private String i_name;
	private String i_owner;
	private Encoding i_encoding;
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s's %s (%s)", WordUtils.capitalize(getOwner()), getName(), getEncoding());
	}
	
	@Override
	public int compareTo(Device o) {
		return toString().compareTo(o.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Device && compareTo((Device) obj) == 0;
	}

	public String getName() {
		return i_name;
	}
	
	public void setName(String name) {
		i_name = name;
	}
	
	public String getOwner() {
		return i_owner;
	}
	
	public void setOwner(String owner) {
		i_owner = owner;
	}
	
	public Encoding getEncoding() {
		return i_encoding;
	}
	
	public void setEncoding(Encoding encoding) {
		i_encoding = encoding;
	}
}
