package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.DataDao;
import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.hibernate.service.DataService;

@Transactional
public class DataServiceImpl implements DataService {

	private static final String ALLOWABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
	
	private File i_dataStorageDirectory;
	private DataDao i_dataDao;
	
	@PostConstruct
	public void initialise() {
		getDataStorageDirectory().mkdirs();
	}
	
	@Override
	public DataBean createDataBean(String name, String extension) throws IOException {
		extension = StringUtils.trimToEmpty(extension).toLowerCase();
		name = StringUtils.trimToEmpty(name).toLowerCase();
		name = StringUtils.left(name, 200 - extension.length() - 1);
		StringBuilder cleanNameBuilder = new StringBuilder();
		for (char c : name.toCharArray()) {
			cleanNameBuilder.append(ALLOWABLE_CHARACTERS.indexOf(c) < 0?'-':c);
		}
		name = cleanNameBuilder.toString();
		DataBean dataBean = new DataBean();
		File dir = getDataStorageDirectory();
		File dataFile;
		do {
			dataFile = new File(dir, UUID.randomUUID().toString() + "-" + name + "." + extension);
		} while (dataFile.exists());
		dataFile.createNewFile();
		dataBean.setFilename(dataFile.getName());
		getDataDao().store(dataBean);
		return dataBean;
	}

	@Override
	public File findFile(DataBean dataBean) {
		return findFile(dataBean.getFilename());
	}
	
	@Override
	public File findFile(String filename) {
		return new File(getDataStorageDirectory(), filename);
	}

	public DataDao getDataDao() {
		return i_dataDao;
	}

	public void setDataDao(DataDao dataDao) {
		i_dataDao = dataDao;
	}

	public File getDataStorageDirectory() {
		return i_dataStorageDirectory;
	}

	public void setDataStorageDirectory(File dataStorageDirectory) {
		i_dataStorageDirectory = dataStorageDirectory;
	}
}
