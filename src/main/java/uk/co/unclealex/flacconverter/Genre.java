/**
 * 
 */
package uk.co.unclealex.flacconverter;

/**
 * @author alex
 *
 */
public class Genre implements Comparable<Genre>{

	private String i_name;

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return i_name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		i_name = name;
	}
	
	public int compareTo(Genre o) {
		return getName().compareTo(o.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Genre) && getName().equals(((Genre) obj).getName());
	}
}
