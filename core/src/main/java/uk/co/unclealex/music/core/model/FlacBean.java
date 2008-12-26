package uk.co.unclealex.music.core.model;

import uk.co.unclealex.music.core.visitor.FlacVisitor;

public interface FlacBean {

	public void accept(FlacVisitor flacVisitor);
}
