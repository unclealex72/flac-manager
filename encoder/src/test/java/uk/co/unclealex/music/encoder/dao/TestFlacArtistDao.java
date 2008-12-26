package uk.co.unclealex.music.encoder.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.music.core.dao.FlacArtistDao;
import uk.co.unclealex.music.core.model.FlacArtistBean;

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
	public SortedSet<FlacArtistBean> getArtistsBeginningWith(char c) {
		SortedSet<FlacArtistBean> flacArtistsBeans = new TreeSet<FlacArtistBean>();
		CollectionUtils.select(getAll(), createStartsWithPredicate(c), flacArtistsBeans);
		return flacArtistsBeans;
	}
	
	@Override
	public int countArtistsBeginningWith(char c) {
		return getArtistsBeginningWith(c).size();
	}
	
	protected Predicate<FlacArtistBean> createStartsWithPredicate(final char c) {
		return new Predicate<FlacArtistBean>() {
			@Override
			public boolean evaluate(FlacArtistBean flacArtistBean) {
				return flacArtistBean.getCode().charAt(0) == Character.toUpperCase(c);
			}
		};
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
