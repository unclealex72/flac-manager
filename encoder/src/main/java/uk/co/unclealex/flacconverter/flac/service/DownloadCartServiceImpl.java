package uk.co.unclealex.flacconverter.flac.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.flac.dao.FlacAlbumDao;
import uk.co.unclealex.flacconverter.flac.dao.FlacArtistDao;
import uk.co.unclealex.flacconverter.flac.model.DownloadCartBean;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;
import uk.co.unclealex.flacconverter.flac.model.FlacBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.flac.visitor.FlacTrackVisitor;
import uk.co.unclealex.flacconverter.flac.visitor.FlacVisitor;
import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.music.core.writer.TrackStream;
import uk.co.unclealex.music.core.writer.TrackWriter;
import uk.co.unclealex.music.core.writer.TrackWriterFactory;
import uk.co.unclealex.music.core.writer.TrackWritingException;

public class DownloadCartServiceImpl implements DownloadCartService {

	private TrackWriterFactory i_trackWriterFactory;
	private EncodedTrackDao i_encodedTrackDao;
	private FlacAlbumDao i_flacAlbumDao;
	private FlacArtistDao i_flacArtistDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	
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
			DownloadCartBean downloadCartBean, final EncoderBean encoderBean) {
		final EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		final SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
		FlacTrackVisitor visitor = new FlacTrackVisitor() {
			@Override
			public void visit(FlacTrackBean flacTrackBean) {
				encodedTrackBeans.add(encodedTrackDao.findByUrlAndEncoderBean(flacTrackBean.getUrl(), encoderBean));
			}
			
			@Override
			public FlacAlbumBean refresh(FlacAlbumBean flacAlbumBean) {
				return getFlacAlbumDao().findById(flacAlbumBean.getId());
			}
			@Override
			public FlacArtistBean refresh(FlacArtistBean flacArtistBean) {
				return getFlacArtistDao().findById(flacArtistBean.getId());
			}
		};
		for (FlacBean flacBean : downloadCartBean.getSelections()) {
			flacBean.accept(visitor);
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

	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

}
