package uk.co.unclealex.music.core.spring;

import java.beans.Introspector;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class BeanNameGenerator extends AnnotationBeanNameGenerator {

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String beanName = super.buildDefaultBeanName(definition);
		beanName = StringUtils.removeStart(beanName, "spring");
		beanName = StringUtils.removeStart(beanName, "hibernate");
		beanName = StringUtils.removeEnd(beanName, "Impl");
		return Introspector.decapitalize(beanName);
	}
}
