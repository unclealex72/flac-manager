package uk.co.unclealex.music.core.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections15.ComparatorUtils;

public class AlbumCoverComparator implements Comparator<AlbumCoverBean> {

	private Comparator<AlbumCoverBean> i_delegate;
	
	@SuppressWarnings("unchecked")
	public AlbumCoverComparator() {
		Comparator<AlbumCoverBean> flacAlbumComparator = new Comparator<AlbumCoverBean>() {
			@Override
			public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {				
				return compareNullHigh(o1.getFlacAlbumPath(), o2.getFlacAlbumPath());
			}
		};
		Comparator<AlbumCoverBean> selectedComparator = new Comparator<AlbumCoverBean>() {
			@Override
			public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {
				return compareNullHigh(o1.getDateSelected(), o2.getDateSelected());
			}
		};
		Comparator<AlbumCoverBean> reverseSizeComparator = new Comparator<AlbumCoverBean>() {
			@Override
			public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {
				return compareNullHigh(o2.getAlbumCoverSize(), o1.getAlbumCoverSize());
			}
		};
		Comparator<AlbumCoverBean> urlComparator = new Comparator<AlbumCoverBean>() {
			@Override
			public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {
				return compareNullHigh(o1.getUrl(), o2.getUrl());
			}
		};
		Comparator<AlbumCoverBean> idComparator = new Comparator<AlbumCoverBean>() {
			@Override
			public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {
				return compareNullHigh(o1.getId(), o2.getId());
			}
		};
		List<Comparator<AlbumCoverBean>> comparators = new ArrayList<Comparator<AlbumCoverBean>>();
		comparators.add(flacAlbumComparator);
		comparators.add(selectedComparator);
		comparators.add(reverseSizeComparator);
		comparators.add(urlComparator);
		comparators.add(idComparator);
		
		i_delegate = ComparatorUtils.chainedComparator((Comparator<AlbumCoverBean>[]) comparators.toArray(new Comparator<?>[0]));
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Comparable<?>> int compareNullHigh(T t1, T t2) {
		return ComparatorUtils.nullHighComparator(ComparatorUtils.naturalComparator()).compare(t1, t2);
	}

	@Override
	public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {
		return getDelegate().compare(o1, o2);
	}
	
	public Comparator<AlbumCoverBean> getDelegate() {
		return i_delegate;
	}
}
