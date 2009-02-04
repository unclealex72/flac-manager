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

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.lang.StringUtils;

public class ExtraPropertiesNodeDecorator extends AlteringPropertiesNodeDecorator {

	private Set<Property> i_extraProperties;
	
	public ExtraPropertiesNodeDecorator(Node delegate, Property... extraProperties) {
		super(delegate);
		i_extraProperties = new HashSet<Property>(Arrays.asList(extraProperties));
	}
	
	@Override
	protected List<Property> getAlteredProperties(String namePattern) throws RepositoryException {
		Predicate<Property> nameMatchesPredicate;
		if (namePattern == null) {
			nameMatchesPredicate = new Predicate<Property>() {
				@Override
				public boolean evaluate(Property property) {
					return true;
				}
			};
		}
		else {
			final List<Predicate<Property>> predicates = new ArrayList<Predicate<Property>>();
			for (String pattern : StringUtils.split(namePattern, "|")) {
				final String trimmedPattern = pattern.trim();
				Predicate<Property> predicate;
				if (trimmedPattern.endsWith("*")) {
					final String prefix = trimmedPattern.substring(0, pattern.length() - 1); 
					predicate = new PropertyNamePredicate() {
						@Override
						public boolean evaluate(String propertyName) {
							return propertyName.startsWith(prefix);
						}
					};
				}
				else {
					predicate = new PropertyNamePredicate() {
						@Override
						protected boolean evaluate(String propertyName) {
							return trimmedPattern.equals(propertyName);
						}
					};
				}
				predicates.add(predicate);
			}
			nameMatchesPredicate = new Predicate<Property>() {
				@Override
				public boolean evaluate(Property property) {
					for (Predicate<Property> predicate : predicates) {
						if (predicate.evaluate(property)) {
							return true;
						}
					}
					return false;
				}
			};
		}
		List<Property> alteredProperties = new ArrayList<Property>();
		for (PropertyIterator iter = getOriginalPropertiesWithOptionalNamePattern(namePattern); iter.hasNext(); ) {
			alteredProperties.add(iter.nextProperty());
		}
		CollectionUtils.select(getExtraProperties(), nameMatchesPredicate, alteredProperties);
		return alteredProperties;
	}

	protected abstract class PropertyNamePredicate implements Predicate<Property> {
		
		protected abstract boolean evaluate(String propertyName);
		
		@Override
		public boolean evaluate(Property property) {
			try {
				return evaluate(property.getName());
			}
			catch (RepositoryException e) {
				return false;
			}
		}
	}
	
	public Set<Property> getExtraProperties() {
		return i_extraProperties;
	}
}
