package uk.co.unclealex.music.base.model;

public abstract class IdentifiableBean<T extends IdentifiableBean<T, I>, I extends Comparable<I>>
		extends AbstractEncodedBean<T> implements Identifiable<I> {

	private I i_identifier;

	@Override
	public abstract int compareTo(T o);
	
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.base.model.Identifiable#getIdentifier()
	 */
	public I getIdentifier() {
		return i_identifier;
	}

	public void setIdentifier(I identifier) {
		i_identifier = identifier;
	}
}
