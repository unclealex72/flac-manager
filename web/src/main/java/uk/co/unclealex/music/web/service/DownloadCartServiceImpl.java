package uk.co.unclealex.music.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.unclealex.music.core.dao.EncodedAlbumDao;
import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.music.core.visitor.EncodedTrackVisitor;
import uk.co.unclealex.music.core.visitor.EncodedVisitor;
import uk.co.unclealex.music.core.writer.TrackStream;
import uk.co.unclealex.music.core.writer.TrackWriter;
import uk.co.unclealex.music.core.writer.TrackWriterFactory;
import uk.co.unclealex.music.core.writer.TrackWritingException;
import uk.co.unclealex.music.web.model.DownloadCartBean;

public class DownloadCartServiceImpl implements DownloadCartService {

	private TrackWriterFactory i_trackWriterFactory;
	private EncodedTrackDao i_encodedTrackDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	
	@Override
	public SortedMap<EncodedArtistBean, SortedMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>>> createFullView(
			DownloadCartBean downloadCartBean) {
		final SortedMap<EncodedArtistBean, SortedMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>>> fullView =
			new TreeMap<EncodedArtistBean, SortedMap<EncodedAlbumBean,SortedSet<EncodedTrackBean>>>();
		EncodedVisitor encodedVisitor = new EncodedVisitor() {
			@Override
			public void visit(EncodedArtistBean encodedArtistBean) {
				fullView.put(encodedArtistBean, null);
			}
			@Override
			public void visit(EncodedAlbumBean encodedAlbumBean) {
				EncodedArtistBean encodedArtistBean = encodedAlbumBean.getEncodedArtistBean();
				SortedMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>> albums = getAlbumsForArtist(encodedArtistBean);
				if (albums != null) {
					albums.put(encodedAlbumBean, null);
				}
			}
			@Override
			public void visit(EncodedTrackBean encodedTrackBean) {
				EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
				EncodedArtistBean encodedArtistBean = encodedAlbumBean.getEncodedArtistBean();
				SortedMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>> albums = getAlbumsForArtist(encodedArtistBean);
				if (!albums.containsKey(encodedAlbumBean)) {
					albums.put(encodedAlbumBean, new TreeSet<EncodedTrackBean>());
				}
				SortedSet<EncodedTrackBean> tracks = albums.get(encodedAlbumBean);
				tracks.add(encodedTrackBean);
			}
			
			protected SortedMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>> getAlbumsForArtist(EncodedArtistBean encodedArtistBean) {
				if (!fullView.containsKey(encodedArtistBean)) {
					fullView.put(encodedArtistBean, new TreeMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>>());
				}
			 return fullView.get(encodedArtistBean);
			}
		};
		for (EncodedBean encodedBean : downloadCartBean.getSelections()) {
			encodedBean.accept(encodedVisitor);
		}
		return fullView;
	}

	@Override
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans(
			DownloadCartBean downloadCartBean, final EncoderBean encoderBean) {
		final EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		final SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
		EncodedTrackVisitor visitor = new EncodedTrackVisitor() {
			@Override
			public void visit(EncodedTrackBean encodedTrackBean) {
				encodedTrackBeans.add(encodedTrackDao.findByUrlAndEncoderBean(encodedTrackBean.getFlacUrl(), encoderBean));
			}
			
			@Override
			public EncodedAlbumBean refresh(EncodedAlbumBean encodedAlbumBean) {
				return getEncodedAlbumDao().findById(encodedAlbumBean.getId());
			}
			@Override
			public EncodedArtistBean refresh(EncodedArtistBean encodedArtistBean) {
				return getEncodedArtistDao().findById(encodedArtistBean.getId());
			}
		};
		for (EncodedBean encodedBean : downloadCartBean.getSelections()) {
			encodedBean.accept(visitor);
		}
		return encodedTrackBeans;
	}

	@Override
	public void writeAsZip(DownloadCartBean downloadCartBean,
			String titleFormat, EncoderBean encoderBean, OutputStream out) throws IOException, TrackWritingException {
		SortedSet<EncodedTrackBean> encodedTrackBeans = getEncodedTrackBeans(downloadCartBean, encoderBean);
		TrackWriterFactory trackWriterFactory = getTrackWriterFactory();
		TitleFormatService titleFormatService = getTitleFormatServiceFactory().createTitleFormatService(titleFormat); 
		TrackStream trackStream = trackWriterFactory.createZipTrackStream(out);
		TrackWriter trackWriter = trackWriterFactory.createTrackWriter(trackStream, titleFormatService);
		try {
			trackWriter.create();
			trackWriter.writeAll(encodedTrackBeans);
		}
		finally {
			trackWriter.close();
		}
	}
	
	public TrackWriterFactory getTrackWriterFactory() {
		return i_trackWriterFactory;
	}

	public void setTrackWriterFactory(TrackWriterFactory trackWriterFactory) {
		i_trackWriterFactory = trackWriterFactory;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

}
