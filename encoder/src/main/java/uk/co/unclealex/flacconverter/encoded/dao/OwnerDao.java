package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;

public interface OwnerDao extends EncodingDao<OwnerBean> {

	public OwnerBean findOwnerByNameAndPassword(String username, String encodedPassword);

	public OwnerBean findByName(String name);

}
