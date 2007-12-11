package uk.co.unclealex.music.web.interceptor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class BeanInterceptor implements BeanFactoryAware, Interceptor {

	private BeanFactory beanFactory;
	private String i_beanName;
	private Interceptor i_interceptor;
	
	@Override
	public void init() {
		Interceptor interceptor = (Interceptor) getBeanFactory().getBean(getBeanName(), Interceptor.class);
		setInterceptor(interceptor);
		interceptor.init();
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		return getInterceptor().intercept(invocation);
	}

	@Override
	public void destroy() {
		getInterceptor().destroy();
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public String getBeanName() {
		return i_beanName;
	}

	public void setBeanName(String beanName) {
		i_beanName = beanName;
	}

	public Interceptor getInterceptor() {
		return i_interceptor;
	}

	public void setInterceptor(Interceptor interceptor) {
		i_interceptor = interceptor;
	}


}
