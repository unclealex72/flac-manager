package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedAlbumDao;
import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class AlbumPathComponent extends AbstractPathComponent implements VisiblePathComponent {

	private EncodedAlbumBean i_encodedAlbumBean;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public SortedSet<String> getChildren() {
		EncoderBean encoderBean = getContext().getEncoderBean();
		final String extension = encoderBean.getExtension();
		Transformer<EncodedTrackBean, String> transformer = new Transformer<EncodedTrackBean, String>() {
			@Override
			public String transform(EncodedTrackBean encodedTrackBean) {
				int trackNumber = encodedTrackBean.getTrackNumber();
				StringBuffer buffer = new StringBuffer();
				if (trackNumber < 10) {
					buffer.append('0');
				}
				buffer.append(trackNumber).append(" - ").append(encodedTrackBean.getTitle()).append('.').append(extension);
				return buffer.toString();
			}
		};
		SortedSet<String> trackTitles = new TreeSet<String>();
		Context context = getContext();
		EncodedAlbumBean encodedAlbumBean = context.getEncodedAlbumBean();
		CollectionUtils.collect(
				getEncodedTrackDao().findByAlbumAndEncoderBean(encodedAlbumBean, encoderBean), 
				transformer, trackTitles);
		return trackTitles;
	}

	@Override
	public void setPathComponent(final String pathComponent) throws PathNotFoundException {
		EncodedAlbumBean encodedAlbumBean = 
			getEncodedAlbumDao().findByArtistAndTitle(getContext().getEncodedArtistBean(), pathComponent);
		if (encodedAlbumBean == null) {
			throw new PathNotFoundException(pathComponent);
		}
		setEncodedAlbumBean(encodedAlbumBean);
		getContext().setEncodedAlbumBean(encodedAlbumBean);
	}

	@Override
	public void accept(PathComponentVisitor pathComponentVisitor) {
		pathComponentVisitor.visit(this);
	}
	
	public EncodedAlbumBean getEncodedAlbumBean() {
		return i_encodedAlbumBean;
	}

	public void setEncodedAlbumBean(EncodedAlbumBean encodedAlbumBean) {
		i_encodedAlbumBean = encodedAlbumBean;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	@Required
	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

}
