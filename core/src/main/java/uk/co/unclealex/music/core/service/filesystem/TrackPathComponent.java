package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.spring.Prototype;

@Prototype
@Transactional
public class TrackPathComponent extends AbstractPathComponent implements VisiblePathComponent {

	private static Pattern TRACK_PATTERN = Pattern.compile("([0-9][0-9]) - (.+)\\.(.+?)");
	
	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public SortedSet<String> getChildren() {
		return null;
	}

	@Override
	public void setPathComponent(final String pathComponent) throws PathNotFoundException {
		Matcher matcher = TRACK_PATTERN.matcher(pathComponent);
		Context context = getContext();
		if (matcher.matches()) {
			int trackNumber = Integer.parseInt(matcher.group(1));
			String filename = matcher.group(2);
			String extension = matcher.group(3);
			EncoderBean encoderBean = context.getEncoderBean();
			if (extension.equals(encoderBean.getExtension())) {
				EncodedTrackBean encodedTrackBean = 
					getEncodedTrackDao().findByAlbumAndEncoderBeanAndTrackNumber(
							context.getEncodedAlbumBean(), encoderBean, trackNumber);
				if (encodedTrackBean != null && filename.equals(encodedTrackBean.getFilename())) {
					context.setEncodedTrackBean(encodedTrackBean);
					return;
				}
			}
		}
		throw new PathNotFoundException(pathComponent);
	}

	@Override
	public void accept(PathComponentVisitor pathComponentVisitor) {
		pathComponentVisitor.visit(this);
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

}
