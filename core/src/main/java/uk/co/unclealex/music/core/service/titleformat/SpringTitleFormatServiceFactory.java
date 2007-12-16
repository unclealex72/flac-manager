package uk.co.unclealex.music.core.service.titleformat;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.core.model.DeviceBean;

@Service
public class SpringTitleFormatServiceFactory implements ApplicationContextAware, TitleFormatServiceFactory {

	private ApplicationContext i_applicationContext;
	private String i_titleFormatServiceName = "titleFormatService";
	private TitleFormatFactory i_titleFormatFactory;
	
	public TitleFormatService createTitleFormatService(String titleFormat) {
		TitleFormatService service = 
			(TitleFormatService) getApplicationContext().getBean(getTitleFormatServiceName(), TitleFormatService.class);
		service.setTitleFormat(titleFormat);
		return service;
	}

	public TitleFormatService createTitleFormatService(DeviceBean deviceBean) {
		String titleFormat = deviceBean.getTitleFormat();
		if (titleFormat == null) {
			titleFormat = getTitleFormatFactory().getDefaultTitleFormat();
		}
		return createTitleFormatService(titleFormat);
	}
	
	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

	public String getTitleFormatServiceName() {
		return i_titleFormatServiceName;
	}

	public void setTitleFormatServiceName(String trackFormatServiceName) {
		i_titleFormatServiceName = trackFormatServiceName;
	}

	public TitleFormatFactory getTitleFormatFactory() {
		return i_titleFormatFactory;
	}

	@Required
	public void setTitleFormatFactory(TitleFormatFactory titleFormatFactory) {
		i_titleFormatFactory = titleFormatFactory;
	}
	
}
