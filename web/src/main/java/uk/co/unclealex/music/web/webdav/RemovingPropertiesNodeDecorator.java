package uk.co.unclealex.music.web.webdav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

public class RemovingPropertiesNodeDecorator extends AlteringPropertiesNodeDecorator {

	private Set<String> i_removePropertyNames;

	public RemovingPropertiesNodeDecorator(Node delegate, String... removePropertyNames) {
		super(delegate);
		i_removePropertyNames = new HashSet<String>(Arrays.asList(removePropertyNames));
	}
	
	protected List<Property> getAlteredProperties(String namePattern) throws RepositoryException {
		List<Property> alteredProperties = new ArrayList<Property>();
		PropertyIterator iter = getOriginalPropertiesWithOptionalNamePattern(namePattern);
		Set<String> removePropertyNames = getRemovePropertyNames();
		while (iter.hasNext()) {
			Property property = iter.nextProperty();
			if (!removePropertyNames.contains(property.getName())) {
				alteredProperties.add(property);
			}
		}
		return alteredProperties;
	}
	
	public Set<String> getRemovePropertyNames() {
		return i_removePropertyNames;
	}
}
