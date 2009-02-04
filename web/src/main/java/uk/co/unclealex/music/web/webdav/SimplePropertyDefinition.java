package uk.co.unclealex.music.web.webdav;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.OnParentVersionAction;

public class SimplePropertyDefinition implements PropertyDefinition {

	private Property i_property;
	
	public SimplePropertyDefinition(Property property) {
		super();
		i_property = property;
	}
	
	@Override
	public NodeType getDeclaringNodeType() {
		try {
			return getProperty().getNode().getDefinition().getDeclaringNodeType();
		}
		catch (RepositoryException e) {
			return null;
		}
	}
	
	@Override
	public int getRequiredType() {
		try {
			return getProperty().getType();
		}
		catch (RepositoryException e) {
			// Should not happen
			return PropertyType.UNDEFINED;
		}
	}
	
	@Override
	public Value[] getDefaultValues() {
		try {
			return getProperty().getValues();
		}
		catch (RepositoryException e) {
			// Should not happen.
			return null;
		}
	}
	
	@Override
	public String getName() {
		return getName();
	}
	
	@Override
	public int getOnParentVersion() {
		return OnParentVersionAction.IGNORE;
	}

	@Override
	public String[] getValueConstraints() {
		return new String[0];
	}

	@Override
	public boolean isAutoCreated() {
		return true;
	}

	@Override
	public boolean isMandatory() {
		return true;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}
	
	@Override
	public boolean isProtected() {
		return true;
	}
	
	protected Property getProperty() {
		return i_property;
	}
}
