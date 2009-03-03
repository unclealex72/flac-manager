package uk.co.unclealex.music.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.DataDao;
import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.hibernate.model.KeyedBean;
import uk.co.unclealex.music.base.io.DataManager;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;

@Transactional(rollbackFor=IOException.class)
public abstract class AbstractDataManager<K extends KeyedBean<K>> implements DataManager<K> {

	private File i_encodedMusicStorageDirectory;
	private DataDao i_dataDao;
	
	public void injectData(K keyedBean, KnownLengthInputStream data) throws IOException {
		DataBean dataBean = getDataBean(keyedBean);
		if (dataBean == null) {
			dataBean = new DataBean();
			setDataBean(keyedBean, dataBean);
			getDataDao().store(dataBean);
		}
		File dir = getEncodedMusicStorageDirectory();
		File dataFile = null;
		boolean fileExists = true;
		while (fileExists) {
			dataFile = new File(dir, UUID.randomUUID().toString());
			fileExists = dataFile.exists();
		}
		OutputStream out = new FileOutputStream(dataFile);
		dataBean.setPath(dataFile.getCanonicalPath());
		try {
			IOUtils.copy(data, out);
		}
		finally {
			IOUtils.closeQuietly(out);
		}
		getDao().store(keyedBean);
	}

	public void extractData(K keyedBean, KnownLengthInputStreamCallback callback) throws IOException {
		DataBean dataBean = getDataBean(keyedBean);
		if (dataBean != null) {
			File file = new File(dataBean.getPath());
			KnownLengthInputStream in = new KnownLengthInputStream(new FileInputStream(file), (int) file.length());
			try {
				callback.execute(in);
			}
			finally {
				in.close();
			}
		}
	}
	
	@Override
	public void extractData(int id, KnownLengthInputStreamCallback callback) throws IOException {
		extractData(getDao().findById(id), callback);
	}
	
	protected abstract DataBean getDataBean(K keyedBean);
	protected abstract void setDataBean(K keyedBean, DataBean dataBean);
	
	protected abstract KeyedDao<K> getDao();

	public File getEncodedMusicStorageDirectory() {
		return i_encodedMusicStorageDirectory;
	}

	@Required
	public void setEncodedMusicStorageDirectory(File encodedMusicStorageDirectory) {
		i_encodedMusicStorageDirectory = encodedMusicStorageDirectory;
	}

	public DataDao getDataDao() {
		return i_dataDao;
	}

	public void setDataDao(DataDao dataDao) {
		i_dataDao = dataDao;
	}
}
