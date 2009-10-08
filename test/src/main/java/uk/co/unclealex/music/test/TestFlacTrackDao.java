package uk.co.unclealex.music.test;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public class TestFlacTrackDao implements FlacTrackDao {

	private TestFlacProvider i_testFlacProvider;

	@Override
	public SortedSet<String> getAllUrls() {
		Transformer<FlacTrackBean, String> urlTransformer = new Transformer<FlacTrackBean, String>() {
			@Override
			public String transform(FlacTrackBean flacTrackBean) {
				return flacTrackBean.getUrl();
			}
		};
		return CollectionUtils.collect(getTestFlacProvider().getAllFlacTrackBeans().values(), urlTransformer, new TreeSet<String>());
	}

	@Override
	public FlacTrackBean findByFile(File flacFile) throws IOException {
		return findByUrl(flacFile.toURI().toURL().toString());
	}
	
	@Override
	public FlacTrackBean findByUrl(final String url) {
		return CollectionUtils.find(
				getAll(),
				new Predicate<FlacTrackBean>() {
					@Override
					public boolean evaluate(FlacTrackBean flacTrackBean) {
						return flacTrackBean.getUrl().equals(url);
					}
				});
	}

	@Override
	public SortedSet<FlacTrackBean> getAll() {
		return new TreeSet<FlacTrackBean>(getTestFlacProvider().getAllFlacTrackBeans().values());
	}

	@Override
	public int countTracks() {
		return getAll().size();
	}
	
	@Override
	public FlacTrackBean findById(final int id) {
		return CollectionUtils.find(getAll(),
			new Predicate<FlacTrackBean>() {
				@Override
				public boolean evaluate(FlacTrackBean flacTrackBean) {
					return id == flacTrackBean.getId();
				}
			});
	}

	@Override
	public SortedSet<FlacTrackBean> findTracksStartingWith(final String url) {
		return CollectionUtils.select(
				getAll(),
				new Predicate<FlacTrackBean>() {
					@Override
					public boolean evaluate(FlacTrackBean flacTrackBean) {
						return flacTrackBean.getUrl().startsWith(url);
					}
				},
				new TreeSet<FlacTrackBean>());
	}
	
	@Override
	public long count() {
		return getTestFlacProvider().getAllFlacTrackBeans().size();
	}
	
	@Override
	public void flush() {
		// Do nothing
		
	}

	@Override
	public void clear() {
		// Do nothing
	}

	@Override
	public void dismiss(FlacTrackBean keyedBean) {
		// Do nothing
	}
	
	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	@Required
	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}


}
