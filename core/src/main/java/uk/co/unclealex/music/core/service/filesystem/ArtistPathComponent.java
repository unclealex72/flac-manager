package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class ArtistPathComponent extends AbstractPathComponent implements VisiblePathComponent {

	private EncodedArtistBean i_encodedArtistBean;
	private EncodedArtistDao i_encodedArtistDao;
	
	@Override
	public SortedSet<String> getChildren() {
		Transformer<EncodedAlbumBean, String> transformer = new Transformer<EncodedAlbumBean, String>() {
			@Override
			public String transform(EncodedAlbumBean encodedAlbumBean) {
				return encodedAlbumBean.getTitle();
			}
		};
		SortedSet<String> albumTitles = new TreeSet<String>();
		CollectionUtils.collect(getEncodedArtistBean().getEncodedAlbumBeans(), transformer, albumTitles);
		return albumTitles;
	}

	@Override
	public void setPathComponent(final String pathComponent) throws PathNotFoundException {
		EncodedArtistBean encodedArtistBean = getEncodedArtistDao().findByName(pathComponent);
		if (encodedArtistBean == null) {
			throw new PathNotFoundException(pathComponent);
		}
		setEncodedArtistBean(encodedArtistBean);
		getContext().setEncodedArtistBean(encodedArtistBean);
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

	public EncodedArtistBean getEncodedArtistBean() {
		return i_encodedArtistBean;
	}

	public void setEncodedArtistBean(EncodedArtistBean encodedArtistBean) {
		i_encodedArtistBean = encodedArtistBean;
	}

}
