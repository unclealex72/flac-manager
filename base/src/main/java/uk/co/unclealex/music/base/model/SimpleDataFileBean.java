package uk.co.unclealex.music.base.model;

import java.io.File;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.music.base.visitor.FileVisitor;

public class SimpleDataFileBean implements DataFileBean {

	private String i_path;
	private DataBean i_dataBean;
	
	public SimpleDataFileBean(String path, DataBean dataBean) {
		super();
		i_path = path;
		i_dataBean = dataBean;
	}

	@Override
	public <R, E extends Exception> R accept(FileVisitor<R, E> fileVisitor) {
		return fileVisitor.visit(this);
	}
	
	@Override
	public File getFile() {
		return getDataBean().getFile();
	}
	
	@Override
	public Integer getId() {
		return getDataBean().getId();
	}
	
	public DataBean getDataBean() {
		return i_dataBean;
	}

	public String getPath() {
		return i_path;
	}	
}
