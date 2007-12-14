package uk.co.unclealex.music.core.model;

public abstract class IdentifiableBean<T extends IdentifiableBean<T, I>, I extends Comparable<I>>
		extends KeyedBean<T> {

	private I i_identifier;

	@Override
	public int compareTo(T o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}
	
	public I getIdentifier() {
		return i_identifier;
	}

	public void setIdentifier(I identifier) {
		i_identifier = identifier;
	}
}
