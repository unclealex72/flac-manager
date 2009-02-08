package uk.co.unclealex.music.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import uk.co.unclealex.music.core.io.InputStreamCopier;
import uk.co.unclealex.music.core.io.InputStreamFactory;
import uk.co.unclealex.music.core.io.KnownLengthOutputStream;

public class PictureServlet extends AbstractServlet {

	private InputStreamFactory<Integer> i_inputStreamFactory;
	private InputStreamCopier<Integer> i_inputStreamCopier;
	
	@SuppressWarnings("unchecked")
	@Override
	public void doInit(ServletConfig config) {
		String inputStreamFactoryName = config.getInitParameter("inputStreamFactory");
		InputStreamFactory<Integer> inputStreamFactory = 
			(InputStreamFactory<Integer>) getApplicationContext().getBean(inputStreamFactoryName);
		setEncodedTrackDataExtractor(inputStreamFactory);
	}
	
	@Override
	public void doService(HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		String requestURI = req.getRequestURI();
		String requestString = FilenameUtils.getBaseName(requestURI);
		String extension = FilenameUtils.getExtension(requestURI);
		int albumCoverId = Integer.parseInt(requestString);
		resp.setContentType("image/" + extension);
		KnownLengthOutputStream out = new KnownLengthOutputStream(resp.getOutputStream()) {
			@Override
			protected void setLength(int length) throws IOException {
				resp.setContentLength(length);
			}
		};
		getInputStreamCopier().copy(getInputStreamFactory(), albumCoverId, out);
		out.close();
	}

	@Override
	protected int getAutowireType() {
		return AutowireCapableBeanFactory.AUTOWIRE_NO;
	}
	
	public InputStreamCopier<Integer> getInputStreamCopier() {
		return i_inputStreamCopier;
	}

	@Required
	public void setInputStreamCopier(
			InputStreamCopier<Integer> inputStreamCopier) {
		i_inputStreamCopier = inputStreamCopier;
	}

	public InputStreamFactory<Integer> getInputStreamFactory() {
		return i_inputStreamFactory;
	}

	@Required
	public void setInputStreamFactory(
			InputStreamFactory<Integer> inputStreamFactory) {
		i_inputStreamFactory = inputStreamFactory;
	}
}
