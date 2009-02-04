package uk.co.unclealex.music.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public class PictureServlet extends AbstractServlet {

	private Transformer<Integer, byte[]> i_transformer;
	
	@SuppressWarnings("unchecked")
	@Override
	public void doInit(ServletConfig config) {
		String transformerBeanName = config.getInitParameter("transformer");
		Transformer<Integer, byte[]> transformer = (Transformer<Integer, byte[]>) getApplicationContext().getBean(transformerBeanName);
		setTransformer(transformer);
	}
	
	@Override
	public void doService(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String requestURI = req.getRequestURI();
		String requestString = FilenameUtils.getBaseName(requestURI);
		String extension = FilenameUtils.getExtension(requestURI);
		int albumCoverId = Integer.parseInt(requestString);
		resp.setContentType("image/" + extension);
		byte[] image = getTransformer().transform(albumCoverId);
		resp.setContentLength(image.length);
		ServletOutputStream out = resp.getOutputStream();
		IOUtils.copy(new ByteArrayInputStream(image), out);
		out.close();
	}

	@Override
	protected int getAutowireType() {
		return AutowireCapableBeanFactory.AUTOWIRE_NO;
	}
	
	public Transformer<Integer, byte[]> getTransformer() {
		return i_transformer;
	}

	public void setTransformer(Transformer<Integer, byte[]> transformer) {
		i_transformer = transformer;
	}
}
