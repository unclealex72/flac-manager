package uk.co.unclealex.music.core.pool;

import javax.sql.DataSource;

import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.beans.factory.FactoryBean;

public class PooledDataSource implements FactoryBean {

	private DataSource i_dataSource;
	
	@Override
	public Object getObject() {
		GenericObjectPool pool = new GenericObjectPool();
		pool.setMinEvictableIdleTimeMillis(30000);
		pool.setTimeBetweenEvictionRunsMillis(60000);
		pool.setMaxActive(40);
		DataSourceConnectionFactory dataSourceConnectionFactory = new DataSourceConnectionFactory(getDataSource());
		@SuppressWarnings("unused")
		PoolableConnectionFactory poolableConnectionFactory = 
			new PoolableConnectionFactory(dataSourceConnectionFactory, pool, null, null, false, false);
		return new PoolingDataSource(pool);
	}

	@Override
	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public DataSource getDataSource() {
		return i_dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		i_dataSource = dataSource;
	}

}
