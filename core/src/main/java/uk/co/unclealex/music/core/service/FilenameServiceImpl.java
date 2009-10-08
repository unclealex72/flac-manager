package uk.co.unclealex.music.core.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.SlimServerConfig;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.service.FilenameService;
import uk.co.unclealex.music.base.visitor.EncodedVisitor;

@Transactional
public class FilenameServiceImpl implements FilenameService {

	private SlimServerConfig i_slimServerConfig;

	@Override
	public String createFilename(EncodedBean encodedBean) {
		FilenameExtractingEncodedVisitor visitor = new FilenameExtractingEncodedVisitor();
		String filename = encodedBean.accept(visitor);
		return new ValidFilenameTransformer().transform(filename);
	}
	
	protected class FilenameExtractingEncodedVisitor extends EncodedVisitor<String, Exception> {
		@Override
		public String visit(EncodedAlbumBean encodedAlbumBean) {
			return encodedAlbumBean.getTitle();
		}
		@Override
		public String visit(EncodedArtistBean encodedArtistBean) {
			return removeDefiniteArticle(encodedArtistBean.getName());
		}
		@Override
		public String visit(EncodedTrackBean encodedTrackBean) {
			return encodedTrackBean.getTitle();
		}		
	}

	protected String removeDefiniteArticle(String artist) {
		List<String> definiteArticles = getSlimServerConfig().getDefiniteArticles();
		if (definiteArticles == null) {
			return artist;
		}
		boolean removed = false;
		for (Iterator<String> iter = definiteArticles.iterator(); iter.hasNext() && !removed;) {
			String article = iter.next().trim() + " ";
			if (artist.startsWith(article)) {
				removed = true;
				artist = StringUtils.removeStart(artist, article);
			}
		}
		return artist;
	}

	public SlimServerConfig getSlimServerConfig() {
		return i_slimServerConfig;
	}

	@Required
	public void setSlimServerConfig(SlimServerConfig slimServerConfig) {
		i_slimServerConfig = slimServerConfig;
	}
	

}
