package uk.co.unclealex.music.albumcover.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.service.DataService;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.FlacAlbumDao;
import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.ArtworkTaggingException;
import uk.co.unclealex.music.base.service.ExternalCoverArtException;
import uk.co.unclealex.music.base.service.ExternalCoverArtService;
import uk.co.unclealex.music.covers.ExternalCoverArtImage;

@Transactional
public class AlbumCoverServiceImpl implements AlbumCoverService {

	private static final Logger log = Logger.getLogger(AlbumCoverServiceImpl.class);
	
	private AlbumCoverDao i_albumCoverDao;
	private FlacAlbumDao i_flacAlbumDao;
	private DataService i_dataService;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedTrackDao i_encodedTrackDao;
	private ExternalCoverArtService i_externalCoverArtService;
	private ArtworkTaggingService i_artworkTaggingService;

	@Override
	public Predicate<FlacAlbumBean> createAlbumHasCoverPredicate() {
		final AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		return new Predicate<FlacAlbumBean>() {
			@Override
			public boolean evaluate(FlacAlbumBean flacAlbumBean) {
				String artistCode = flacAlbumBean.getFlacArtistBean().getCode();
				String albumCode = flacAlbumBean.getCode();
				return albumCoverDao.findSelectedCoverForAlbum(artistCode, albumCode) != null;
			}
		};
	}

	@Override
	public SortedSet<AlbumCoverBean> downloadCoversForAlbum(FlacAlbumBean flacAlbumBean) throws ExternalCoverArtException, IOException, ArtworkTaggingException {
		SortedSet<AlbumCoverBean> albumCoverBeans = new TreeSet<AlbumCoverBean>();
		SortedSet<ExternalCoverArtImage> externalCoverArtImages = 
			getExternalCoverArtService().searchForImages(flacAlbumBean.getFlacArtistBean().getName(), flacAlbumBean.getTitle());
		for (Iterator<ExternalCoverArtImage> iter = externalCoverArtImages.iterator(); iter.hasNext(); ) {
			ExternalCoverArtImage externalCoverArtImage = iter.next();
			URL url = externalCoverArtImage.getUrl();
			InputStream in = url.openStream();
			try {
				AlbumCoverBean albumCoverBean =
					saveAndOptionallySelectCover(flacAlbumBean, url.toString(), in, !iter.hasNext());
				albumCoverBeans.add(albumCoverBean);
			}
			finally {
				IOUtils.closeQuietly(in);
			}
		}
		return albumCoverBeans;
	}

	@Override
	public AlbumCoverBean saveAndSelectCover(FlacAlbumBean flacAlbumBean, String imageUrl, InputStream urlStream)
			throws IOException, ArtworkTaggingException {
		return saveAndOptionallySelectCover(flacAlbumBean, imageUrl, urlStream, true);
	}

	protected AlbumCoverBean saveAndOptionallySelectCover(
		FlacAlbumBean flacAlbumBean, String imageUrl, InputStream urlStream, boolean select) throws IOException, ArtworkTaggingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(urlStream, out);
		byte[] buffer = out.toByteArray();
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		AlbumCoverBean albumCoverBean = getAlbumCoverDao().findByUrl(imageUrl);
		if (albumCoverBean == null) {
			albumCoverBean = new AlbumCoverBean();
			albumCoverBean.setUrl(imageUrl);
			String filename = String.format(
					"%s-%s-%dx%d", flacAlbumBean.getFlacArtistBean().getCode(), flacAlbumBean.getCode(), image.getWidth(), image.getHeight());
			albumCoverBean.setCoverDataBean(getDataService().createDataBean(filename, FilenameUtils.getExtension(imageUrl)));
		}
		albumCoverBean.setAlbumCode(flacAlbumBean.getCode());
		albumCoverBean.setAlbumCoverSize((long) image.getWidth() * image.getHeight());
		albumCoverBean.setArtistCode(flacAlbumBean.getFlacArtistBean().getCode());
		albumCoverBean.setDateDownloaded(new Date());
		albumCoverBean.setExtension(FilenameUtils.getExtension(imageUrl).toLowerCase());
		OutputStream dataOutputStream = new FileOutputStream(fileOf(albumCoverBean));
		try {
			IOUtils.copy(new ByteArrayInputStream(buffer), dataOutputStream);
		}
		finally {
			IOUtils.closeQuietly(dataOutputStream);
		}
		albumCoverDao.store(albumCoverBean);
		if (select) {
			selectAlbumCover(albumCoverBean, true);
		}
		return albumCoverBean;
	}
	
	protected File fileOf(AlbumCoverBean albumCoverBean) {
		return getDataService().findFile(albumCoverBean.getCoverDataBean());
	}
	
	@Override
	public void downloadMissing() throws ArtworkTaggingException {
		for (FlacAlbumBean flacAlbumBean : findAlbumsWithoutCovers()) {
			try {
				downloadCoversForAlbum(flacAlbumBean);
			}
			catch (ExternalCoverArtException e) {
				log.warn("Could not download any covers for album " + flacAlbumBean, e);
			}
			catch (IOException e) {
				log.warn("Could not download any covers for album " + flacAlbumBean, e);
			}
		}
	}

	@Override
	public SortedSet<FlacAlbumBean> findAlbumsWithoutCovers() {
		TreeSet<FlacAlbumBean> albumsWithoutCovers = new TreeSet<FlacAlbumBean>();
		CollectionUtils.selectRejected(getFlacAlbumDao().getAll(), createAlbumHasCoverPredicate(), albumsWithoutCovers);
		return albumsWithoutCovers;
	}

	protected SortedSet<AlbumCoverBean> findCoversForAlbum(String artistCode, String albumCode) {
		return getAlbumCoverDao().getCoversForAlbum(artistCode, albumCode);
	}

	@Override
	public SortedSet<AlbumCoverBean> findCoversForAlbum(FlacAlbumBean flacAlbumBean) {
		return findCoversForAlbum(flacAlbumBean.getFlacArtistBean().getCode(), flacAlbumBean.getCode());
	}

	protected AlbumCoverBean findSelectedCoverForAlbum(String artistCode, String albumCode) {
		return getAlbumCoverDao().findSelectedCoverForAlbum(artistCode, albumCode);
	}

	@Override
	public AlbumCoverBean findSelectedCoverForEncodedTrack(EncodedTrackBean encodedTrackBean) {
		EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
		return findSelectedCoverForAlbum(encodedAlbumBean.getEncodedArtistBean().getCode(), encodedAlbumBean.getCode());
	}

	@Override
	public AlbumCoverBean findSelectedCoverForFlacAlbum(FlacAlbumBean flacAlbumBean) {
		return findSelectedCoverForAlbum(flacAlbumBean.getFlacArtistBean().getCode(), flacAlbumBean.getCode());
	}

	@Override
	public boolean removeCoversForMissingAlbum(String artistCode, String albumCode) {
		boolean removed = false;
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		for (AlbumCoverBean albumCoverBean : findCoversForAlbum(artistCode, albumCode)) {
			albumCoverDao.remove(albumCoverBean);
			removed = true;
		}
		return removed;
	}

	@Override
	public void removeUnselectedCovers(FlacAlbumBean flacAlbumBean) {
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		for (AlbumCoverBean albumCoverBean : findCoversForAlbum(flacAlbumBean)) {
			if (albumCoverBean.getDateSelected() != null) {
				albumCoverDao.remove(albumCoverBean);
			}
		}
	}

	@Override
	public void selectAlbumCover(AlbumCoverBean albumCoverBean, boolean updateTimestamp) throws IOException, ArtworkTaggingException {
		AlbumCoverBean currentlySelectedCover = findSelectedCoverForAlbum(albumCoverBean.getArtistCode(), albumCoverBean.getAlbumCode());
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		if (
				albumCoverBean.getDateSelected() == null && 
				(currentlySelectedCover == null || !albumCoverBean.equals(currentlySelectedCover))) {
			if (currentlySelectedCover != null) {
				currentlySelectedCover.setDateSelected(null);
				albumCoverDao.store(currentlySelectedCover);
			}
			albumCoverBean.setDateSelected(new Date());
			albumCoverDao.store(albumCoverBean);
			albumCoverDao.flush();
			updateTags(albumCoverBean, updateTimestamp);
		}
	}

	protected void updateTags(AlbumCoverBean albumCoverBean, boolean updateTimestamp) throws IOException, ArtworkTaggingException {
		File albumCoverFile = fileOf(albumCoverBean);
		Transformer<FlacAlbumBean, SortedSet<FlacTrackBean>> flacAlbumTransformer = new Transformer<FlacAlbumBean, SortedSet<FlacTrackBean>>() {
			@Override
			public SortedSet<FlacTrackBean> transform(FlacAlbumBean flacAlbumBean) {
				return flacAlbumBean.getFlacTrackBeans();
			}
		};
		Transformer<FlacTrackBean, File> flacTrackTransformer = new Transformer<FlacTrackBean, File>() {
			@Override
			public File transform(FlacTrackBean flacTrackBean) {
				return flacTrackBean.getFile();
			}
		};
		Transformer<FlacTrackBean, String> flacTrackNameTransformer = new Transformer<FlacTrackBean, String>() {
			@Override
			public String transform(FlacTrackBean flacTrackBean) {
				FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
				FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
				return String.format(
						"%s: %s, %02d - %s.%s", 
						flacArtistBean.getName(), flacAlbumBean.getTitle(), flacTrackBean.getTrackNumber(), flacTrackBean.getTitle(), "flac");
			}
		};
		Transformer<EncodedAlbumBean, SortedSet<EncodedTrackBean>> encodedAlbumTransformer = 
			new Transformer<EncodedAlbumBean, SortedSet<EncodedTrackBean>>() {
			@Override
			public SortedSet<EncodedTrackBean> transform(EncodedAlbumBean encodedAlbumBean) {
				return getEncodedTrackDao().findByAlbumAndEncoderCoverSupported(encodedAlbumBean, true);
			}
		};
		Transformer<EncodedTrackBean, File> encodedTrackTransformer = new Transformer<EncodedTrackBean, File>() {
			@Override
			public File transform(EncodedTrackBean encodedTrackBean) {
				return getDataService().findFile(encodedTrackBean.getTrackDataBean());
			}
		};
		String artistCode = albumCoverBean.getArtistCode();
		String albumCode = albumCoverBean.getAlbumCode();
		FlacAlbumBean flacAlbumBean = getFlacAlbumDao().findByArtistAndAlbum(artistCode, albumCode);
		doUpdateTags(
				albumCoverFile, 
				flacAlbumBean,
				flacAlbumTransformer, flacTrackTransformer, flacTrackNameTransformer, updateTimestamp);
		EncodedAlbumBean encodedAlbumBean = getEncodedAlbumDao().findByArtistCodeAndCode(artistCode, albumCode);
		doUpdateTags(
				albumCoverFile, 
				encodedAlbumBean,
				encodedAlbumTransformer, encodedTrackTransformer, 
				new EncodedTrackNameTransformer(), updateTimestamp);
	}

	protected <A, T> void doUpdateTags(
			File albumCoverFile, A album, Transformer<A, SortedSet<T>> albumTransformer, 
			Transformer<T, File> trackTransformer, Transformer<T, String> trackNameTransformer, boolean updateTimestamp) throws IOException, ArtworkTaggingException {
		if (album != null) {
			ArtworkTaggingService artworkTaggingService = getArtworkTaggingService();
			for (T track : albumTransformer.transform(album)) {
				artworkTaggingService.updateTag(
						albumCoverFile, trackTransformer.transform(track),
						trackNameTransformer.transform(track), updateTimestamp);
			}
		}
	}

	protected class EncodedTrackNameTransformer implements Transformer<EncodedTrackBean, String> {
		@Override
		public String transform(EncodedTrackBean encodedTrackBean) {
			EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
			EncodedArtistBean encodedArtistBean = encodedAlbumBean.getEncodedArtistBean();
			return String.format(
					"%s: %s, %02d - %s.%s", 
					encodedArtistBean.getName(), encodedAlbumBean.getTitle(), encodedTrackBean.getTrackNumber(), 
					encodedTrackBean.getTitle(), encodedTrackBean.getEncoderBean().getExtension());
		}
	}
	
	@Override
	public int tagAll(boolean updateTimestamps) {
		int count = 0;
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			try {
				if (tagFile(encodedTrackBean, updateTimestamps)) {
					count++;
				}
			}
			catch (IOException e) {
				log.warn("Could not tag track " + encodedTrackBean, e);
			}
			catch (ArtworkTaggingException e) {
				log.warn("Could not tag track " + encodedTrackBean, e);
			}
		}
		return count;
	}
	
	@Override
	public boolean tagFile(EncodedTrackBean encodedTrackBean, boolean updateTimestamp) throws IOException, ArtworkTaggingException {
		EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
		AlbumCoverBean albumCoverBean = 
			getAlbumCoverDao().findSelectedCoverForAlbum(encodedAlbumBean.getEncodedArtistBean().getCode(), encodedAlbumBean.getCode());
		if (albumCoverBean == null) {
			return false;
		}
		getArtworkTaggingService().updateTag(
				fileOf(albumCoverBean), 
				getDataService().findFile(encodedTrackBean.getTrackDataBean()), 
				new EncodedTrackNameTransformer().transform(encodedTrackBean),
				updateTimestamp);
		return true;
	}
	
	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}

	public DataService getDataService() {
		return i_dataService;
	}

	public void setDataService(DataService dataService) {
		i_dataService = dataService;
	}

	public ExternalCoverArtService getExternalCoverArtService() {
		return i_externalCoverArtService;
	}

	public void setExternalCoverArtService(ExternalCoverArtService externalCoverArtService) {
		i_externalCoverArtService = externalCoverArtService;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public ArtworkTaggingService getArtworkTaggingService() {
		return i_artworkTaggingService;
	}

	public void setArtworkTaggingService(ArtworkTaggingService artworkTaggingService) {
		i_artworkTaggingService = artworkTaggingService;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

}
