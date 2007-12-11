package uk.co.unclealex.music.core.security.service;

import uk.co.unclealex.acegi.User;
import uk.co.unclealex.acegi.UserService;
import uk.co.unclealex.music.core.encoded.dao.OwnerDao;

public class FlacConverterUserService implements UserService {

	private OwnerDao i_ownerDao;
	
	@Override
	public User findUserByUsernameAndPassword(String username, String encodedPassword) {
		return getOwnerDao().findOwnerByNameAndPassword(username, encodedPassword);
	}

	@Override
	public String[] getRolesForUser(User user) {
		return new String[] { "ROLE_USER" };
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

}
