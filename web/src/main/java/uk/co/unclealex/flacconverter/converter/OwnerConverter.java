package uk.co.unclealex.flacconverter.converter;

import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.encoded.dao.OwnerDao;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;

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
