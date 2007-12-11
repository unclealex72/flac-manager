package uk.co.unclealex.music.core.spring;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class HibernateSessionBinder {

	private boolean i_participate;
	private FlushMode i_flushMode;
	private SessionFactory i_sessionFactory;
		
	public HibernateSessionBinder(SessionFactory sessionFactory) {
		this(sessionFactory, null);
	}
	
	public HibernateSessionBinder(
			SessionFactory sessionFactory, FlushMode flushMode) {
		super();
		i_flushMode = flushMode;
		i_sessionFactory = sessionFactory;
	}

	public void bind() {
		setParticipate(false);
		SessionFactory sessionFactory = getSessionFactory();
		if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
			// Do not modify the Session: just set the participate flag.
			setParticipate(true);
		}
		else {
			Session session = getSession(sessionFactory);
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		}	
	}
	
	public void unbind() {
		SessionFactory sessionFactory = getSessionFactory();
		if (!isParticipate()) {
			SessionHolder sessionHolder =
					(SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			closeSession(sessionHolder.getSession(), sessionFactory);
		}		
	}
	
	protected Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		FlushMode flushMode = getFlushMode();
		if (flushMode != null) {
			session.setFlushMode(flushMode);
		}
		return session;
	}

	protected void closeSession(Session session, SessionFactory sessionFactory) {
		SessionFactoryUtils.closeSession(session);
	}

	public boolean isParticipate() {
		return i_participate;
	}
	public void setParticipate(boolean participate) {
		i_participate = participate;
	}
	
	public FlushMode getFlushMode() {
		return i_flushMode;
	}
	public void setFlushMode(FlushMode flushMode) {
		i_flushMode = flushMode;
	}
	
	public SessionFactory getSessionFactory() {
		return i_sessionFactory;
	}
	protected void setSessionFactory(SessionFactory sessionFactory) {
		i_sessionFactory = sessionFactory;
	}

}
