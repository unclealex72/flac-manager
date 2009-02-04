package uk.co.unclealex.spring;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SessionDuringStartupSessionFactoryBean implements FactoryBean, ApplicationListener {

	private SessionFactory i_sessionFactory;
	
	@PostConstruct
	public void initialise() {
		SessionFactory sessionFactory = getSessionFactory();
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			SessionFactory sessionFactory = getSessionFactory();
			SessionHolder sessionHolder =
				(SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
	
	@Override
	public Object getObject() {
		return getSessionFactory();
	}

	@Override
	public Class<? extends SessionFactory> getObjectType() {
		SessionFactory sessionFactory = getSessionFactory();
		return (sessionFactory != null) ? sessionFactory.getClass() : SessionFactory.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public SessionFactory getSessionFactory() {
		return i_sessionFactory;
	}

	@Required
	public void setSessionFactory(SessionFactory sessionFactory) {
		i_sessionFactory = sessionFactory;
	}
}
