package uk.co.unclealex.flacconverter.encoded.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatFactory;
import uk.co.unclealex.flacconverter.encoded.writer.TrackWritingException;
import uk.co.unclealex.flacconverter.flac.model.DownloadCartBean;
import uk.co.unclealex.flacconverter.flac.service.DownloadCartService;

public class ZipDownloadFilter implements Filter {

	private static final Logger log = Logger.getLogger(ZipDownloadFilter.class);
	
	private DownloadCartService i_downloadCartService;
	private EncoderDao i_encoderDao;
	private TitleFormatFactory i_titleFormatFactory;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String filename = req.getServletPath();
    
    if (!"zip".equals(FilenameUtils.getExtension(filename))) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    DownloadCartService downloadCartService = getDownloadCartService(); 
    EncoderDao encoderDao = getEncoderDao();
    EncoderBean encoderBean = encoderDao.findByExtension(req.getParameter("extension"));
    String titleFormat = req.getParameter("titleFormat");
    if (titleFormat == null) {
    	titleFormat = getTitleFormatFactory().getDefaultTitleFormat();
    }
    HttpSession session = req.getSession();
    DownloadCartBean downloadCartBean = (DownloadCartBean) session.getAttribute("cart:sessionDownloadCartBean");     
    resp.setContentType("application/zip; name=" + filename);
    resp.setHeader("Content-disposition", "attachment; filename=" + filename);
    OutputStream out = resp.getOutputStream();
    try {
			downloadCartService.writeAsZip(downloadCartBean, titleFormat, encoderBean, out);
		}
		catch (TrackWritingException e) {
			for (Collection<IOException> causes : e.getIoExceptionsByTrackStream().values()) {
				for (IOException cause : causes) {
					log.error(cause);
				}
			}
			throw new ServletException(e);
		}
    out.flush();
    out.close();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext wac =
				WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
		wac.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
	}

	@Override
	public void destroy() {
	}

	public DownloadCartService getDownloadCartService() {
		return i_downloadCartService;
	}

	public void setDownloadCartService(DownloadCartService downloadCartService) {
		i_downloadCartService = downloadCartService;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public TitleFormatFactory getTitleFormatFactory() {
		return i_titleFormatFactory;
	}

	public void setTitleFormatFactory(TitleFormatFactory titleFormatFactory) {
		i_titleFormatFactory = titleFormatFactory;
	}

}
