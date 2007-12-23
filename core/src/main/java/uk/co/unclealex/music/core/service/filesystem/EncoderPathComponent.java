package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.EncodedService;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class EncoderPathComponent extends AbstractPathComponent implements VisiblePathComponent {

	private EncoderDao i_encoderDao;
	private EncodedService i_encodedService;
	
	@Override
	public SortedSet<String> getChildren() {
		SortedSet<String> children = new TreeSet<String>();
		CollectionUtils.collect(
			getEncodedService().getAllFirstLettersOfArtists(),
			new Transformer<Character, String>() {
				@Override
				public String transform(Character c) {
					return	c.toString();
				}
			},
			children);
		return children;
	}

	@Override
	public void setPathComponent(String pathComponent) throws PathNotFoundException {
		EncoderBean encoderBean = getEncoderDao().findByExtension(pathComponent);
		if (encoderBean == null) {
			throw new PathNotFoundException(pathComponent);
		}
		getContext().setEncoderBean(encoderBean);
	}

	@Override
	public void accept(PathComponentVisitor pathComponentVisitor) {
		pathComponentVisitor.visit(this);
	}
	
	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	@Required
	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}
}
