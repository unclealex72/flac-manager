package uk.co.unclealex.music.core.model;

public abstract class IdentifiableBean<T extends IdentifiableBean<T, I>, I extends Comparable<I>>
		extends KeyedBean<T> implements Identifiable<I> {

	private I i_identifier;

	@Override
	public int compareTo(T o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.core.model.Identifiable#getIdentifier()
	 */
	public I getIdentifier() {
		return i_identifier;
	}

	public void setIdentifier(I identifier) {
		i_identifier = identifier;
	}
}
