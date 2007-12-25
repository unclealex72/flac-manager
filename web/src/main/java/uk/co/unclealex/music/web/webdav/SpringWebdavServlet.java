package uk.co.unclealex.music.web.webdav;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.slide.webdav.WebdavServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringWebdavServlet extends WebdavServlet {

	private static ApplicationContext s_applicationContext;
	
	public static ApplicationContext getApplicationContext() {
		return s_applicationContext;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		s_applicationContext = 
			WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		super.init(config);
	}
}
