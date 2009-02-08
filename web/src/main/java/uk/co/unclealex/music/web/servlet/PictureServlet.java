package uk.co.unclealex.music.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import uk.co.unclealex.music.core.io.DataExtractor;
import uk.co.unclealex.music.core.io.InputStreamCopier;
import uk.co.unclealex.music.core.io.KnownLengthOutputStream;

public class PictureServlet extends AbstractServlet {

	private DataExtractor i_dataExtractor;
	private InputStreamCopier i_inputStreamCopier;
	
	@Override
	public void doInit(ServletConfig config) throws ServletException {
		String dataExtractorName = config.getInitParameter("dataExtractor");
		if (dataExtractorName == null) {
			throw new ServletException("Please supply a dataExtractor");
		}
		Object dataExtractor = getApplicationContext().getBean(dataExtractorName);
		if (dataExtractor == null) {
			throw new ServletException("There is no bean called '" + dataExtractorName + "'");
		}
		if (dataExtractor instanceof DataExtractor) {
			setDataExtractor((DataExtractor) dataExtractor);
		}
		else {
			throw new ServletException("Bean '" + dataExtractorName + "' cannot be cast to " + DataExtractor.class);
		}
	}
	
	@Override
	public void doService(HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		String requestURI = req.getRequestURI();
		String requestString = FilenameUtils.getBaseName(requestURI);
		String extension = FilenameUtils.getExtension(requestURI);
		int albumCoverId = Integer.parseInt(requestString);
		resp.setContentType("image/" + extension);
		KnownLengthOutputStream<ServletOutputStream> out = new KnownLengthOutputStream<ServletOutputStream>(resp.getOutputStream()) {
			@Override
			protected void setLength(int length) throws IOException {
				resp.setContentLength(length);
			}
		};
		getInputStreamCopier().copy(getDataExtractor(), albumCoverId, out);
		out.close();
	}

	@Override
	protected int getAutowireType() {
		return AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
	}
	
	public DataExtractor getDataExtractor() {
		return i_dataExtractor;
	}

	public void setDataExtractor(DataExtractor dataExtractor) {
		i_dataExtractor = dataExtractor;
	}

	public InputStreamCopier getInputStreamCopier() {
		return i_inputStreamCopier;
	}

	public void setInputStreamCopier(InputStreamCopier inputStreamCopier) {
		i_inputStreamCopier = inputStreamCopier;
	}

}
