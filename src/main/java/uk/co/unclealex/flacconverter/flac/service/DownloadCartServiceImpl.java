package uk.co.unclealex.flacconverter.flac.service;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.flac.model.DownloadCartBean;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;
import uk.co.unclealex.flacconverter.flac.model.FlacBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.flac.visitor.FlacVisitor;

public class DownloadCartServiceImpl implements DownloadCartService {

	@Override
	public SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>> createFullView(
			DownloadCartBean downloadCartBean) {
		final SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>> fullView =
			new TreeMap<FlacArtistBean, SortedMap<FlacAlbumBean,SortedSet<FlacTrackBean>>>();
		FlacVisitor flacVisitor = new FlacVisitor() {
			@Override
			public void visit(FlacArtistBean flacArtistBean) {
				fullView.put(flacArtistBean, null);
			}
			@Override
			public void visit(FlacAlbumBean flacAlbumBean) {
				FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
				SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>> albums = getAlbumsForArtist(flacArtistBean);
				if (albums != null) {
					albums.put(flacAlbumBean, null);
				}
			}
			@Override
			public void visit(FlacTrackBean flacTrackBean) {
				FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
				FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
				SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>> albums = getAlbumsForArtist(flacArtistBean);
				if (!albums.containsKey(flacAlbumBean)) {
					albums.put(flacAlbumBean, new TreeSet<FlacTrackBean>());
				}
				SortedSet<FlacTrackBean> tracks = albums.get(flacAlbumBean);
				tracks.add(flacTrackBean);
			}
			
			protected SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>> getAlbumsForArtist(FlacArtistBean flacArtistBean) {
				if (!fullView.containsKey(flacArtistBean)) {
					fullView.put(flacArtistBean, new TreeMap<FlacAlbumBean, SortedSet<FlacTrackBean>>());
				}
			 return fullView.get(flacArtistBean);
			}
		};
		for (FlacBean flacBean : downloadCartBean.getSelections()) {
			flacBean.accept(flacVisitor);
		}
		return fullView;
	}

	@Override
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans(
			DownloadCartBean downloadCartBean) {
		// TODO Auto-generated method stub
		return null;
	}

}
