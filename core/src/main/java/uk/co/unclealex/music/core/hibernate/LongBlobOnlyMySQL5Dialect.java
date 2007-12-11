package uk.co.unclealex.music.core.hibernate;

import org.hibernate.dialect.MySQL5Dialect;

public class LongBlobOnlyMySQL5Dialect extends MySQL5Dialect {

	@Override
	protected void registerColumnType(int code, int capacity, String name) {
		name = transform(name);
		super.registerColumnType(code, capacity, name);
	}
	
	@Override
	public String getTableTypeString() {
		return " ENGINE=MyISAM";
	}

	@Override
	protected void registerColumnType(int code, String name) {
		name = transform(name);
		super.registerColumnType(code, name);
	}

	private String transform(String name) {
		return name.endsWith("blob")?"longblob":name;
	}
}
