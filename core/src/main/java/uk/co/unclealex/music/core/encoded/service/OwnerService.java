package uk.co.unclealex.music.core.encoded.service;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;
import uk.co.unclealex.music.core.encoded.model.EncoderBean;
import uk.co.unclealex.music.core.encoded.model.OwnerBean;

@Transactional
public interface OwnerService {

	public SortedSet<EncodedTrackBean> getOwnedEncodedTracks(OwnerBean ownerBean, EncoderBean encoderBean);
}
