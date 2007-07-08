package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import org.apache.commons.collections15.CollectionUtils;

import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

public class TestFlacArtistDao implements FlacArtistDao {

	private TestFlacProvider i_testFlacProvider;
	
	@Override
	public FlacArtistBean findByCode(String code) {
		return CollectionUtils.find(getAll(), getTestFlacProvider().getCodedPredicate(code));
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
