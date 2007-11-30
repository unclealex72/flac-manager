package uk.co.unclealex.flacconverter.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.BackgroundProcess;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.flacconverter.spring.HibernateSessionBinder;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class HibernateSessionBackgroundProcess extends BackgroundProcess implements StrutsStatics {
	
	private HibernateSessionBinder i_hibernateSessionBinder;
	private SessionFactory i_sessionFactory;
	
	public HibernateSessionBackgroundProcess(String threadName,
			ActionInvocation invocation, int threadPriority) {
		super(threadName, invocation, threadPriority);
		setSessionFactory(lookupSessionFactory());
	}

	@Override
	protected void beforeInvocation() {
		HibernateSessionBinder binder = new HibernateSessionBinder(getSessionFactory());
		setHibernateSessionBinder(binder);
		binder.bind();
	}

	@Override
	protected void afterInvocation() {
		getHibernateSessionBinder().unbind();
	}
	
	public SessionFactory lookupSessionFactory() {
    ActionContext context = getInvocation().getInvocationContext();

    HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);

		WebApplicationContext wac =
			WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
		return (SessionFactory) wac.getBean("sessionFactory", SessionFactory.class);
	}

	public HibernateSessionBinder getHibernateSessionBinder() {
		return i_hibernateSessionBinder;
	}

	public void setHibernateSessionBinder(
			HibernateSessionBinder hibernateSessionBinder) {
		i_hibernateSessionBinder = hibernateSessionBinder;
	}

	public SessionFactory getSessionFactory() {
		return i_sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		i_sessionFactory = sessionFactory;
	}
}
