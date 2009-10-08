package uk.co.unclealex.music.test;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.music.base.dao.FlacAlbumDao;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;

public class TestFlacAlbumDao implements FlacAlbumDao {

	private TestFlacProvider i_testFlacProvider;
	
	@Override
	public FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName) {
		TestFlacProvider testFlacProvider = getTestFlacProvider();
		FlacArtistBean flacArtistBean =
			CollectionUtils.find(
					testFlacProvider.getAllFlacArtistBeans().values(),
					testFlacProvider.createCodedPredicate(artistName.toUpperCase()));
		if (flacArtistBean == null) {
			return null;
		}
		else {
			return CollectionUtils.find(
					flacArtistBean.getFlacAlbumBeans(),
					testFlacProvider.createCodedPredicate(albumName.toUpperCase()));
		}
	}

	@Override
	public FlacAlbumBean findByCode(String code) {
		return CollectionUtils.find(getAll(), getTestFlacProvider().createCodedPredicate(code));
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
	public long count() {
		return getTestFlacProvider().getAllFlacAlbumBeans().size();
	}
	
	@Override
	public SortedSet<FlacAlbumBean> getAll() {
		return new TreeSet<FlacAlbumBean>(getTestFlacProvider().getAllFlacAlbumBeans().values());
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}

}
