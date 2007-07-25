package uk.co.unclealex.flacconverter.hibernate;

import org.hibernate.dialect.MySQL5InnoDBDialect;

public class LongBlobOnlyMySQL5Dialect extends MySQL5InnoDBDialect {

	@Override
	protected void registerColumnType(int code, int capacity, String name) {
		name = transform(name);
		super.registerColumnType(code, capacity, name);
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
