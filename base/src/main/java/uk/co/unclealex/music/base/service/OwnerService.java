package uk.co.unclealex.music.base.service;

import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;

public interface OwnerService {

	public SortedMap<OwnerBean, SortedSet<FlacTrackBean>> resolveOwnershipByFiles();
}
