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
		return getTestFlacProvider().getAllFlacTrackBeans();
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
	public void flush() {
		// Do nothing
		
	}

	@Override
	public void dismiss(FlacTrackBean keyedBean) {
		// Do nothing
	}
	
	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}


}