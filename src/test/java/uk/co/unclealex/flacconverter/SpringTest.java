package uk.co.unclealex.flacconverter;

import org.springframework.test.AbstractTransactionalSpringContextTests;

public abstract class SpringTest extends AbstractTransactionalSpringContextTests {

	public SpringTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
		setDependencyCheck(false);
	}
		
	@Override
	protected abstract String[] getConfigLocations();	
}
