package uk.co.unclealex.music.core.service;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.model.OwnerBean;

@Transactional
public interface OwnerService {

	public SortedSet<EncodedTrackBean> getOwnedEncodedTracks(OwnerBean ownerBean, EncoderBean encoderBean);
}
