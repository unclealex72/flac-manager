package uk.co.unclealex.music.core.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

public class MethodLoggingBeanFactory implements FactoryBean {

	private Object i_bean;
	private Class<?> i_objectType;
	private boolean i_singleton = true;
	
	@Override
	public Object getObject() throws Exception {
		final Logger log = Logger.getLogger(getObjectType());
		final Object bean = getBean();
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				log.info("In: " + method.getName() + ": " + StringUtils.join(args, ", "));
				Object val = method.invoke(bean, args);
				String out;
				if (val == null) {
					out = "null";
				}
				else if (val.getClass().getComponentType() != null) {
					out = StringUtils.join((Object[]) val, ", ");
				}
				else {
					out = val.toString();
				}
				log.info("Out: " + method.getName() + ": " + out);
				return val;
			}
		};
		return Proxy.newProxyInstance(bean.getClass().getClassLoader(), new Class[] { getObjectType() }, handler);
	}

	public boolean isSingleton() {
		return i_singleton;
	}

	public void setSingleton(boolean singleton) {
		i_singleton = singleton;
	}

	public Object getBean() {
		return i_bean;
	}

	public void setBean(Object bean) {
		i_bean = bean;
	}

	public Class<?> getObjectType() {
		return i_objectType;
	}

	public void setObjectType(Class<?> objectType) {
		i_objectType = objectType;
	}


}
