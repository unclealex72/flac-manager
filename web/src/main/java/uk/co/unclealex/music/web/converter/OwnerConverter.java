package uk.co.unclealex.music.web.converter;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.dao.OwnerDao;
import uk.co.unclealex.music.core.model.OwnerBean;

@Conversion
public class OwnerConverter extends KeyedConverter<OwnerBean> {

	private OwnerDao i_ownerDao;
	
	@Override
	protected KeyedReadOnlyDao<OwnerBean> getDao() {
		return getOwnerDao();
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

}
