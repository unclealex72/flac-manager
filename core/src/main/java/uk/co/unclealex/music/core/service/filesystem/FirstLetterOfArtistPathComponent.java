package uk.co.unclealex.music.core.service.filesystem;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class FirstLetterOfArtistPathComponent extends AbstractPathComponent implements VisiblePathComponent {

	private EncodedArtistDao i_encodedArtistDao;
	private String i_firstLetter;
	
	@Override
	public SortedSet<String> getChildren() {
		SortedSet<EncodedArtistBean> encodedArtistBeans = getEncodedArtistDao().findByFirstLetter(getFirstLetter());
		SortedSet<String> artistNames = new TreeSet<String>();
		CollectionUtils.collect(
			encodedArtistBeans,
			new Transformer<EncodedArtistBean, String>() {
				@Override
				public String transform(EncodedArtistBean encodedArtistBean) {
					return encodedArtistBean.getName();
				}
			},
			artistNames);
		return artistNames;
	}

	@Override
	public void setPathComponent(String pathComponent) throws PathNotFoundException {
		Collection<String> firstLetters = 
			CollectionUtils.collect(getEncodedArtistDao().getAll(), new FirstLetterOfArtistTransformer());
		if (!firstLetters.contains(pathComponent)) {
			throw new PathNotFoundException(pathComponent);
		}
		setFirstLetter(pathComponent);
	}

	@Override
	public void accept(PathComponentVisitor pathComponentVisitor) {
		pathComponentVisitor.visit(this);
	}
	
	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public String getFirstLetter() {
		return i_firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		i_firstLetter = firstLetter;
	}

}
