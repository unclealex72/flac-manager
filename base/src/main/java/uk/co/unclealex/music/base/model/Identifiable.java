package uk.co.unclealex.music.base.model;

public interface Identifiable<I extends Comparable<I>> {

	public I getIdentifier();

}