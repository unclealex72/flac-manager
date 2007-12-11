package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.music.encoder.flac.dao.FlacAlbumDao;
import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;

public class TestFlacAlbumDao implements FlacAlbumDao {

	private TestFlacProvider i_testFlacProvider;
	
	@Override
	public FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName) {
		TestFlacProvider testFlacProvider = getTestFlacProvider();
		FlacArtistBean flacArtistBean =
			CollectionUtils.find(
					testFlacProvider.getAllFlacArtistBeans(),
					testFlacProvider.getCodedPredicate(artistName.toUpperCase()));
		if (flacArtistBean == null) {
			return null;
		}
		else {
			return CollectionUtils.find(
					flacArtistBean.getFlacAlbumBeans(),
					testFlacProvider.getCodedPredicate(albumName.toUpperCase()));
		}
	}

	@Override
	public FlacAlbumBean findByCode(String code) {
		return CollectionUtils.find(getAll(), getTestFlacProvider().getCodedPredicate(code));
	}

	@Override
	public FlacAlbumBean findById(final int id) {
		return CollectionUtils.find(getAll(),
			new Predicate<FlacAlbumBean>() {
				@Override
				public boolean evaluate(FlacAlbumBean flacAlbumBean) {
					return id == flacAlbumBean.getId();
				}
			});
	}
	
	@Override
	public void dismiss(FlacAlbumBean keyedBean) {
		// Do nothing
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
	public SortedSet<FlacAlbumBean> getAll() {
		return getTestFlacProvider().getAllFlacAlbumBeans();
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}

}
