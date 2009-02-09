package uk.co.unclealex.music.core.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.service.DevicesWriter;
import uk.co.unclealex.music.base.service.DevicesWriterFactory;

@Service
public class SpringDevicesWriterFactory implements DevicesWriterFactory,
		ApplicationContextAware {

	private ApplicationContext i_applicationContext;
	private String i_beanName = "devicesWriter";
	
	@Override
	public DevicesWriter create() {
		return (DevicesWriter) getApplicationContext().getBean(getBeanName(), DevicesWriter.class);
	}

	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

	public String getBeanName() {
		return i_beanName;
	}

	public void setBeanName(String beanName) {
		i_beanName = beanName;
	}

}
