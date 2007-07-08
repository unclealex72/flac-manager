package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class TestFlacTrackDao implements FlacTrackDao {

	private TestFlacProvider i_testFlacProvider;

	@Override
	public FlacTrackBean findByUrl(final String url) {
		return CollectionUtils.find(
				getAllTracks(),
				new Predicate<FlacTrackBean>() {
					@Override
					public boolean evaluate(FlacTrackBean flacTrackBean) {
						return flacTrackBean.getUrl().equals(url);
					}
				});
	}

	@Override
	public SortedSet<FlacTrackBean> getAllTracks() {
		return getTestFlacProvider().getAllFlacTrackBeans();
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}
}
