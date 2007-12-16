package uk.co.unclealex.music.core.model;

public interface Identifiable<I extends Comparable<I>> {

	public I getIdentifier();

}