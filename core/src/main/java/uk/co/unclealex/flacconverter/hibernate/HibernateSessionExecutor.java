package uk.co.unclealex.flacconverter.hibernate;

import java.util.concurrent.Executor;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;

import uk.co.unclealex.flacconverter.spring.HibernateSessionBinder;

public class HibernateSessionExecutor implements Executor {

	private SessionFactory i_sessionFactory;
	private boolean i_asynchronous = true;
	
	@Override
	public void execute(final Runnable command) {
		Runnable sessionBoundCommand = new Runnable() {
			@Override
			public void run() {
				HibernateSessionBinder binder =
					new HibernateSessionBinder(getSessionFactory(), FlushMode.AUTO);
				binder.bind();
				try {
					command.run();
				}
				finally {
					binder.unbind();
				}
			}
		};
		Thread thread = new Thread(sessionBoundCommand);
		thread.start();
		if (!isAsynchronous()) {
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public SessionFactory getSessionFactory() {
		return i_sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		i_sessionFactory = sessionFactory;
	}

	public boolean isAsynchronous() {
		return i_asynchronous;
	}

	public void setAsynchronous(boolean asynchronous) {
		i_asynchronous = asynchronous;
	}
}
