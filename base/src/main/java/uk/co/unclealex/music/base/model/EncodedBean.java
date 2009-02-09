package uk.co.unclealex.music.base.model;

import uk.co.unclealex.music.base.visitor.EncodedVisitor;

public interface EncodedBean {

	public String getFilename();
	public void setFilename(String transform);

	public void accept(EncodedVisitor encodedVisitor);
}
