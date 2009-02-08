package uk.co.unclealex.music.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class AbstractServlet extends HttpServlet {

	private ApplicationContext i_applicationContext;
	
	@Override
	public final void init(ServletConfig config) throws ServletException {
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		setApplicationContext(applicationContext);
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, getAutowireType(), false);
		try {
			doInit(config);
		} 
		catch (Throwable t) {
			if (t instanceof ServletException) {
				throw (ServletException) t;
			}
			throw new ServletException(t);
		}
	}
	
	public void doInit(ServletConfig config) throws Exception {
		// Do nothing by default.
	}
	
	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			doService(req, resp);
		}
		catch (IOException e) {
			throw e;
		}
		catch (Throwable t) {
			throw new ServletException(t);
		}
	}
	
	protected int getAutowireType() {
		return AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
	}
	
	public abstract void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception;

	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}
}
