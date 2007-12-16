package uk.co.unclealex.music.encoder.model;

import uk.co.unclealex.music.encoder.visitor.FlacVisitor;

public interface FlacBean {

	public void accept(FlacVisitor flacVisitor);
}
