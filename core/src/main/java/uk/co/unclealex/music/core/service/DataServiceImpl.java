package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.DataDao;
import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.music.base.service.DataService;

@Transactional
public class DataServiceImpl implements DataService {

	private File i_encodedMusicStorageDirectory;
	private DataDao i_dataDao;
	
	@Override
	public DataBean createDataBean() throws IOException {
		DataBean dataBean = new DataBean();
		File dir = getEncodedMusicStorageDirectory();
		File dataFile = null;
		boolean fileExists = true;
		while (fileExists) {
			dataFile = new File(dir, UUID.randomUUID().toString());
			fileExists = dataFile.exists();
		}
		dataFile.createNewFile();
		dataBean.setPath(dataFile.getCanonicalPath());
		getDataDao().store(dataBean);
		return dataBean;
	}

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
