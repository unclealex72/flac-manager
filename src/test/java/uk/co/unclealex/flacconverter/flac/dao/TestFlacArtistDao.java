package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

public class TestFlacArtistDao implements FlacArtistDao {

	private TestFlacProvider i_testFlacProvider;
	
	@Override
	public FlacArtistBean findByCode(String code) {
		return CollectionUtils.find(getAll(), getTestFlacProvider().getCodedPredicate(code));
	}

	@Override
	public FlacArtistBean findById(final int id) {
		return CollectionUtils.find(getAll(),
			new Predicate<FlacArtistBean>() {
				@Override
				public boolean evaluate(FlacArtistBean flacArtistBean) {
					return id == flacArtistBean.getId();
				}
			});
	}

	@Override
	public void flush() {
		// Do nothing
	}

	@Override
	public void dismiss(FlacArtistBean flacArtistBean) {
		// Do nothing
	}

	@Override
	public SortedSet<FlacArtistBean> getAll() {
		return getTestFlacProvider().getAllFlacArtistBeans();
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}

}
