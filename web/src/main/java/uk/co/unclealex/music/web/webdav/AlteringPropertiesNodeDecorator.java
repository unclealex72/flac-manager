package uk.co.unclealex.music.web.webdav;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

public abstract class AlteringPropertiesNodeDecorator extends NodeDecorator {

	public AlteringPropertiesNodeDecorator(Node delegate) {
		super(delegate);
	}

	protected abstract List<Property> getAlteredProperties(String namePattern) throws RepositoryException;
	
	protected PropertyIterator getOriginalPropertiesWithOptionalNamePattern(String namePattern)
			throws RepositoryException {
				return namePattern == null?super.getProperties():super.getProperties(namePattern);
			}

	protected PropertyIterator createPropertyIterator(final List<Property> properties) {
		final ListIterator<Property> iter = properties.listIterator();
		PropertyIterator propertyIter = new PropertyIterator() {
			@Override
			public long getPosition() {
				return iter.nextIndex();
			}
			@Override
			public long getSize() {
				return properties.size();
			}
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}
			@Override
			public Object next() {
				return nextProperty();
			}
			@Override
			public Property nextProperty() {
				return iter.next();
			}
			@Override
			public void remove() {
				iter.remove();
			}
			@Override
			public void skip(long skipNum) {
				for (long idx = 0; idx < skipNum; idx++) {
					iter.next();
				}
			}
		};
		return propertyIter;
	}

	@Override
	public PropertyIterator getProperties() throws RepositoryException {
		return createPropertyIterator(getAlteredProperties(null));
	}

	@Override
	public PropertyIterator getProperties(String namePattern) throws RepositoryException {
		return createPropertyIterator(getAlteredProperties(namePattern));
	}

	@Override
	public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException {
		int slashPos = relPath.indexOf('/');
		if (slashPos < 0) {
			Property property = null;
			for (Iterator<Property> iter = getAlteredProperties(null).iterator(); property == null && iter.hasNext(); ) {
				Property thisProperty = iter.next();
				if (relPath.equals(thisProperty.getName())) {
					property = thisProperty;
				}
			}
			if (property == null) {
				throw new PathNotFoundException(relPath);
			}
			return property;
		}
		else {
			return super.getProperty(relPath);
		}
	}

	@Override
	public boolean hasProperties() throws RepositoryException {
		return getProperties().hasNext();
	}
	
	@Override
	public boolean hasProperty(String relPath) throws RepositoryException {
		try {
			getProperty(relPath);
			return true;
		}
		catch (PathNotFoundException e) {
			return false;
		}
	}
	
}
