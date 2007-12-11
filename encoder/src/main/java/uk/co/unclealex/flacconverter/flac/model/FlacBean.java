package uk.co.unclealex.flacconverter.flac.model;

import uk.co.unclealex.music.encoder.flac.visitor.FlacVisitor;

public interface FlacBean {

	public void accept(FlacVisitor flacVisitor);
}
