package uk.co.unclealex.music.core.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.SpeechProviderEnum;
import uk.co.unclealex.music.base.service.SpeechWriter;
import uk.co.unclealex.music.base.service.SpeechWriterFactory;

@Service
public class SpringSpeechWriterFactory implements SpeechWriterFactory, ApplicationContextAware {

	private ApplicationContext i_applicationContext;
	private Map<SpeechProviderEnum, String> i_speechWriterBeanNamesByEnum = new HashMap<SpeechProviderEnum, String>();
	
	@PostConstruct
	public void initialise() {
		Map<SpeechProviderEnum, String> speechWriterBeanNamesByEnum = getSpeechWriterBeanNamesByEnum();
		speechWriterBeanNamesByEnum.put(SpeechProviderEnum.ROCKBOX, "rockboxSpeechWriter");
		speechWriterBeanNamesByEnum.put(SpeechProviderEnum.NONE, "nullSpeechWriter");
	}
	
	@Override
	public SpeechWriter createSpeechWriter(DeviceBean deviceBean) {
		String beanName = getSpeechWriterBeanNamesByEnum().get(deviceBean.getSpeechProviderEnum());
		if (beanName == null) {
			return null;
		}
		return (SpeechWriter) getApplicationContext().getBean(beanName, SpeechWriter.class);
	}

	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

	public Map<SpeechProviderEnum, String> getSpeechWriterBeanNamesByEnum() {
		return i_speechWriterBeanNamesByEnum;
	}

	public void setSpeechWriterBeanNamesByEnum(Map<SpeechProviderEnum, String> speechWriterBeanNamesByEnum) {
		i_speechWriterBeanNamesByEnum = speechWriterBeanNamesByEnum;
	}

}
