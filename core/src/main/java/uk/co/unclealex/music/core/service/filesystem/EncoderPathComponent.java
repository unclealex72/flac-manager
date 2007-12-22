package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class EncoderPathComponent extends AbstractPathComponent implements VisiblePathComponent {

	private EncoderDao i_encoderDao;
	private EncodedArtistDao i_encodedArtistDao;
	
	@Override
	public SortedSet<String> getChildren() {
		SortedSet<EncodedArtistBean> encodedArtistBeans = getEncodedArtistDao().getAll();
		SortedSet<String> firstLetters = new TreeSet<String>();
		CollectionUtils.collect(
			encodedArtistBeans, 
			new FirstLetterOfArtistTransformer(),
			firstLetters);
		return firstLetters;
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

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

}
