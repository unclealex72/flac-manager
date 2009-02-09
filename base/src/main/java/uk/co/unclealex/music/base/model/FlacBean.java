package uk.co.unclealex.music.base.model;

import uk.co.unclealex.music.base.visitor.FlacVisitor;

public interface FlacBean {

	public void accept(FlacVisitor flacVisitor);
}
