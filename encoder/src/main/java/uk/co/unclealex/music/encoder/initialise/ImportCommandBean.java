package uk.co.unclealex.music.encoder.initialise;

import java.io.File;

import uk.co.unclealex.music.base.service.CommandBean;

public class ImportCommandBean extends CommandBean<ImportCommandBean> {

	private File i_file;

	
	public ImportCommandBean() {
		// This is the end of work command.
	}

	public ImportCommandBean(File file) {
		super();
		i_file = file;
	}

	@Override
	public int compareTo(ImportCommandBean o) {
		return getFile().compareTo(o.getFile());
	}
	
	@Override
	public boolean isEndOfWorkBean() {
		return getFile() == null;
	}
	
	public File getFile() {
		return i_file;
	}
	
}
