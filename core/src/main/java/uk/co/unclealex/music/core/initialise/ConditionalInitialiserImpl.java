package uk.co.unclealex.music.core.initialise;

import java.io.IOException;

import org.apache.log4j.Logger;

import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.initialise.Initialiser;

public class ConditionalInitialiserImpl implements ConditionalInitialiser {

	private static Logger log = Logger.getLogger(ConditionalInitialiserImpl.class);
	
	private Initialiser i_initialiser;
	private OwnerDao i_ownerDao;
	
	@Override
	public void initialise() throws IOException {
		if (getOwnerDao().count() == 0) {
			log.info("No owners found. Initialising.");
			getInitialiser().initialise();
		}
	}

	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

}
