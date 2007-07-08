package uk.co.unclealex.flacconverter.encoded.transformer;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class FlacAlbumToEncodedTracksTransformer extends 
		ToEncodedTracksTransformer<FlacAlbumBean, SortedSet<EncodedTrackBean>>{

	@Override
	public SortedSet<EncodedTrackBean> transform(FlacAlbumBean flacAlbumBean) {
		SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		EncoderBean encoderBean = getEncoderBean();
		for (FlacTrackBean flacTrackBean : flacAlbumBean.getFlacTrackBeans()) {
			EncodedTrackBean encodedTrackBean =
				encodedTrackDao.findByUrlAndEncoderBean(flacTrackBean.getUrl(), encoderBean);
			if (encodedTrackBean != null) {
				encodedTrackBeans.add(encodedTrackBean);
			}
		}
		return encodedTrackBeans;
	}
}
