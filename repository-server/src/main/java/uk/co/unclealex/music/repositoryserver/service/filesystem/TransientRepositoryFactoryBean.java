/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.io.IOException;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.TransientRepository.RepositoryFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * FactoryBean for creating Jackrabbit's TransientRepository (i.e. repository are initialized for the
 * first session and closed once the last session is closed.
 * 
 * @author Costin Leau
 * @since 0.5
 */
public class TransientRepositoryFactoryBean implements FactoryBean {
	
	private RepositoryFactory i_repositoryFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springmodules.jcr.jackrabbit.RepositoryFactoryBean#createRepository()
	 */
	@Override
	public Object getObject() throws IOException {
		return new TransientRepository(getRepositoryFactory());
	}

	@Override
	public Class<?> getObjectType() {
		return TransientRepository.class;
	}
	
	@Override
	public boolean isSingleton() {
		return false;
	}
	
	public RepositoryFactory getRepositoryFactory() {
		return i_repositoryFactory;
	}

	@Required
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		i_repositoryFactory = repositoryFactory;
	}

}
