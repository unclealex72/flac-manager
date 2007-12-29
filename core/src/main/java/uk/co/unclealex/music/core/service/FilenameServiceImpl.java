package uk.co.unclealex.music.core.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.SlimServerConfig;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.visitor.EncodedVisitor;

@Service
@Transactional
public class FilenameServiceImpl implements FilenameService {

	private SlimServerConfig i_slimServerConfig;

	@Override
	public String createFilename(EncodedBean encodedBean) {
		FilenameExtractingEncodedVisitor visitor = new FilenameExtractingEncodedVisitor();
		encodedBean.accept(visitor);
		return new ValidFilenameTransformer().transform(visitor.filename);
	}
	
	protected class FilenameExtractingEncodedVisitor extends EncodedVisitor {
		public String filename;
		@Override
		public void visit(EncodedAlbumBean encodedAlbumBean) {
			filename = encodedAlbumBean.getTitle();
		}
		@Override
		public void visit(EncodedArtistBean encodedArtistBean) {
			filename = removeDefiniteArticle(encodedArtistBean.getName());
		}
		@Override
		public void visit(EncodedTrackBean encodedTrackBean) {
			filename = encodedTrackBean.getTitle();
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
