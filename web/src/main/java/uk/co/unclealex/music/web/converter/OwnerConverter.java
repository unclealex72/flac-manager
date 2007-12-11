package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.flacconverter.KeyedConverter;
import uk.co.unclealex.music.web.encoded.dao.KeyedDao;
import uk.co.unclealex.music.web.encoded.dao.OwnerDao;
import uk.co.unclealex.music.web.encoded.model.OwnerBean;

public class OwnerConverter extends KeyedConverter<OwnerBean> {

	private OwnerDao i_ownerDao;
	
	@Override
	protected KeyedDao<OwnerBean> getDao() {
		return getOwnerDao();
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

}
