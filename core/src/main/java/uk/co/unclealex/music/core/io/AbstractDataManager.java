package uk.co.unclealex.music.core.io;

import java.io.IOException;

import uk.co.unclealex.music.base.dao.KeyedDao;
import uk.co.unclealex.music.base.io.DataManager;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.model.KeyedBean;

public abstract class AbstractDataManager<K extends KeyedBean<K>> implements DataManager<K> {

	public void injectData(K keyedBean, KnownLengthInputStream data) throws IOException {
		doInjectData(keyedBean, data);
		KeyedDao<K> dao = getDao();
		dao.store(keyedBean);
		// Make triply sure that the new track bean is fully persisted.
		dao.flush();
		dao.dismiss(keyedBean);
		dao.findById(keyedBean.getId());
	}

	protected abstract void doInjectData(K keyedBean, KnownLengthInputStream data) throws IOException;

	protected abstract KeyedDao<K> getDao();
}
