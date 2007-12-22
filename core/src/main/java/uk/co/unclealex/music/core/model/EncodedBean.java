package uk.co.unclealex.music.core.model;

import uk.co.unclealex.music.core.visitor.EncodedVisitor;

public interface EncodedBean {

	public void accept(EncodedVisitor encodedVisitor);
}
