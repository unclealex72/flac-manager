package uk.co.unclealex.music.albumcover.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.xml.ws.Holder;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.image.service.ImageService;
import uk.co.unclealex.music.core.dao.AlbumCoverDao;
import uk.co.unclealex.music.core.dao.FlacAlbumDao;
import uk.co.unclealex.music.core.model.AlbumCoverBean;
import uk.co.unclealex.music.core.model.AlbumCoverSize;
import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.service.FlacAlbumService;

import com.amazon.webservices.awsecommerceservice.Errors;
import com.amazon.webservices.awsecommerceservice.ImageSet;
import com.amazon.webservices.awsecommerceservice.Item;
import com.amazon.webservices.awsecommerceservice.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice.Items;
import com.amazon.webservices.awsecommerceservice.OperationRequest;
import com.amazon.webservices.awsecommerceservice.Errors.Error;
import com.amazon.webservices.awsecommerceservice.Item.ImageSets;

@Service
@Transactional
public class AlbumCoverServiceImpl implements AlbumCoverService {

	private static final Logger log = Logger.getLogger(AlbumCoverServiceImpl.class);
	
	private AmazonService i_amazonService;
	private Transformer<ImageSet, AlbumCoverBean> i_imageSetTransformer;
	private FlacAlbumService i_flacAlbumService;
	private FlacAlbumDao i_flacAlbumDao;
	private AlbumCoverDao i_albumCoverDao;
	private ImageService i_imageService;
	private Predicate<FlacAlbumBean> i_albumHasCoversPredicate;
	private int i_thumbnailSize = 50;
	
	@PostConstruct
	public void initialise() {
		Predicate<FlacAlbumBean> albumHasCoversPredicate = new Predicate<FlacAlbumBean>() {
			@Override
			public boolean evaluate(FlacAlbumBean flacAlbumBean) {
				String pathForFlacAlbum = getFlacAlbumService().getPathForFlacAlbum(flacAlbumBean);
				return getAlbumCoverDao().albumPathHasCovers(pathForFlacAlbum);
			}
		};
		setAlbumHasCoversPredicate(albumHasCoversPredicate);
	}
	
	@Override
	public Predicate<FlacAlbumBean> createAlbumHasCoverPredicate() {
		return getAlbumHasCoversPredicate();
	}
	
	@Override
	public SortedSet<FlacAlbumBean> findAlbumsWithoutCovers() {
		SortedSet<FlacAlbumBean> albumsWithoutCovers = new TreeSet<FlacAlbumBean>();
		CollectionUtils.selectRejected(getFlacAlbumDao().getAll(), getAlbumHasCoversPredicate(), albumsWithoutCovers);
		return albumsWithoutCovers;
	}
	
	@Override
	public SortedSet<AlbumCoverBean> downloadCoversForAlbum(FlacAlbumBean flacAlbumBean) {
		Transformer<ImageSet, AlbumCoverBean> imageSetTransformer = getImageSetTransformer();
		Set<String> foundUrls = new HashSet<String>();
		boolean isFirst = true;
		String flacAlbumPath = getPathForFlacAlbum(flacAlbumBean);
		String marketplaceDomain = null;
		AmazonService amazonService = getAmazonService();
		String awsAccessKeyId = amazonService.getAccessKey();
		String subscriptionId = amazonService.getSubscriberId();
		String associateTag = null;
		String xmlEscaping = "Single";
		String validate = "False";
		ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
		itemSearchRequest.setSearchIndex("Music");
		itemSearchRequest.setArtist(flacAlbumBean.getFlacArtistBean().getName());
		itemSearchRequest.setTitle(flacAlbumBean.getTitle());
		itemSearchRequest.setMerchantId("All");
		itemSearchRequest.getResponseGroup().add("Medium");
		List<ItemSearchRequest> request = Collections.singletonList(itemSearchRequest);
		Holder<OperationRequest> operationRequestHolder = new Holder<OperationRequest>();
		Holder<List<Items>> itemsHolder = new Holder<List<Items>>();
		amazonService.itemSearch(
				marketplaceDomain, awsAccessKeyId, subscriptionId, associateTag, xmlEscaping, 
				validate, itemSearchRequest, request, operationRequestHolder, itemsHolder);
		List<Items> items = itemsHolder.value;
		for (Items itemsElement : items) {
			Errors errors = itemsElement.getRequest().getErrors();
			if (errors != null && !errors.getError().isEmpty()) {
				Predicate<Error> isNoExactMatchesErrorPredicate = new Predicate<Error>() {
					@Override
					public boolean evaluate(Error error) {
						return "AWS.ECommerceService.NoExactMatches".equals(error.getCode());
					}
				};
				if (CollectionUtils.find(errors.getError(), isNoExactMatchesErrorPredicate) != null) {
					return new TreeSet<AlbumCoverBean>();
				}
				Transformer<Error, String> transformer = new Transformer<Error, String>() {
					@Override
					public String transform(Error error) {
						return error.getCode() + ": " + error.getMessage();
					}
				};
				String errorMessage = StringUtils.join(CollectionUtils.collect(errors.getError(), transformer).iterator(), "\n");
				throw new IllegalArgumentException(errorMessage);
			}
			for (Item item : itemsElement.getItem()) {
				for (ImageSets imageSets : item.getImageSets()) {
					for (ImageSet imageSet : imageSets.getImageSet()) {
						AlbumCoverBean albumCoverBean = imageSetTransformer.transform(imageSet);
						if (albumCoverBean != null) {
							String url = albumCoverBean.getUrl();
							if (foundUrls.add(url)) {
								log.info("Found " + url + " for album " + flacAlbumBean.getTitle() + " by " + flacAlbumBean.getFlacArtistBean().getName());
								try {
									downloadAndStoreAlbumCover(albumCoverBean, flacAlbumPath, url, null, isFirst);
								} catch (IOException e) {
									log.error("Url " + url + " could not be downloaded.");
								}
								isFirst = false;
							}
						}
					}
				}
				
			}
		}
		return findCoversForAlbum(flacAlbumBean);
	}

	protected void downloadAndStoreAlbumCover(AlbumCoverBean albumCoverBean,
			String flacAlbumPath, String url, InputStream urlInputStream, boolean selected) throws IOException {
		albumCoverBean.setUrl(url);
		byte[] cover = downloadUrl(url, urlInputStream);
		albumCoverBean.setCover(cover);
		byte[] thumbnail = createThumbnail(cover);
		albumCoverBean.setThumbnail(thumbnail);
		albumCoverBean.setExtension(FilenameUtils.getExtension(url));
		albumCoverBean.setFlacAlbumPath(flacAlbumPath);
		Date now = new Date();
		albumCoverBean.setDateDownloaded(now);
		if (selected) {
			selectAlbumCover(albumCoverBean, now);
		}
		else {
			getAlbumCoverDao().store(albumCoverBean);
		}
	}

	protected byte[] createThumbnail(byte[] cover) throws IOException {
		Color background = new Color(0, 0, 0, 0);
		BufferedImage sourceImage = ImageIO.read(new ByteArrayInputStream(cover));
		BufferedImage thumbnailImage =
			getImageService().resize(
				sourceImage, new Dimension(getThumbnailSize(), getThumbnailSize()), background);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(thumbnailImage, "png", out);
		out.close();
		return out.toByteArray();
	}

	@Override
	public void createThumbnails() throws IOException {
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		for (AlbumCoverBean albumCoverBean : albumCoverDao.getAll()) {
			if (albumCoverBean.getThumbnail() == null) {
				log.info("Creating thumbnail for " + albumCoverBean.getFlacAlbumPath());
				albumCoverBean.setThumbnail(createThumbnail(albumCoverBean.getCover()));
				albumCoverDao.store(albumCoverBean);
			}
		}
	}
	
	@Override
	public void saveSelectedAlbumCovers() {
		for (FlacAlbumBean flacAlbumBean : getFlacAlbumDao().getAll()) {
			try {
				saveSelectedAlbumCover(flacAlbumBean);
			}
			catch (IOException e) {
				log.error("Could not save the cover for album " + flacAlbumBean.getTitle() + " by " + flacAlbumBean.getFlacArtistBean().getName(), e);
			}
		}
	}

	protected void saveSelectedAlbumCover(FlacAlbumBean flacAlbumBean) throws IOException {
		AlbumCoverBean albumCoverBean = findSelectedCoverForFlacAlbum(flacAlbumBean);
		if (albumCoverBean != null) {
			saveAlbumCoverBean(albumCoverBean);
		}
	}

	protected void saveAlbumCoverBean(AlbumCoverBean albumCoverBean)
			throws FileNotFoundException, IOException {
		File f = new File(albumCoverBean.getFlacAlbumPath(), "cover." + albumCoverBean.getExtension());
		log.info("Saving " + f);
		OutputStream out = null;
		try {
			out = new FileOutputStream(f);
			IOUtils.copy(new ByteArrayInputStream(albumCoverBean.getCover()), out);
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void downloadAndSaveCoversForAlbums(Collection<FlacAlbumBean> flacAlbumBeans) {
		for (FlacAlbumBean flacAlbumBean : flacAlbumBeans) {
			downloadAndSaveCoversForAlbum(flacAlbumBean);
		}
	}
	
	protected void downloadAndSaveCoversForAlbum(FlacAlbumBean flacAlbumBean) {
		// Dont do anything if covers already exist.
		SortedSet<AlbumCoverBean> findCoversForAlbum = findCoversForAlbum(flacAlbumBean);
		if (findCoversForAlbum != null && !findCoversForAlbum.isEmpty()) {
			return;
		}
		downloadCoversForAlbum(flacAlbumBean);
		try {
			saveSelectedAlbumCover(flacAlbumBean);
		}
		catch (IOException e) {
			log.error("Could not save the cover for album " + flacAlbumBean.getTitle() + " by " + flacAlbumBean.getFlacArtistBean().getName(), e);
		}
	}

	@Override
	public void removeUnselectedCovers(FlacAlbumBean flacAlbumBean) {
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		for (AlbumCoverBean albumCoverBean : albumCoverDao.getCoversForAlbumPath(getPathForFlacAlbum(flacAlbumBean))) {
			if (albumCoverBean.getDateSelected() == null) {
				albumCoverDao.remove(albumCoverBean);
			}
		}
	}

	@Override
	@Transactional(rollbackFor=IOException.class)
	public void selectAlbumCover(AlbumCoverBean albumCoverBean) throws IOException {
		selectAlbumCover(albumCoverBean, new Date());
	}
	
	protected void selectAlbumCover(AlbumCoverBean albumCoverBean, Date now) throws IOException {
		if (albumCoverBean.getDateSelected() != null) {
			return;
		}
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		SortedSet<AlbumCoverBean> coversForAlbumPath = 
			new TreeSet<AlbumCoverBean>(albumCoverDao.getCoversForAlbumPath(albumCoverBean.getFlacAlbumPath()));
		coversForAlbumPath.add(albumCoverBean);
		for (AlbumCoverBean existingAlbumCoverBean : coversForAlbumPath) {
			boolean selected = ObjectUtils.equals(existingAlbumCoverBean.getId(), albumCoverBean.getId());
			existingAlbumCoverBean.setDateSelected(selected?now:null);
			albumCoverDao.store(existingAlbumCoverBean);
		}
		saveAlbumCoverBean(albumCoverBean);
	}
	
	@Override
	public AlbumCoverBean findSelectedCoverForFlacAlbum(FlacAlbumBean flacAlbumBean) {
		return getAlbumCoverDao().findSelectedCoverForAlbumPath(getPathForFlacAlbum(flacAlbumBean));
	}
	
	protected String getPathForFlacAlbum(FlacAlbumBean flacAlbumBean) {
		return getFlacAlbumService().getPathForFlacAlbum(flacAlbumBean);
	}
	
	@Override
	public SortedSet<AlbumCoverBean> findCoversForAlbum(
			FlacAlbumBean flacAlbumBean) {
		return getAlbumCoverDao().getCoversForAlbumPath(getPathForFlacAlbum(flacAlbumBean));
	}
	
	protected byte[] downloadUrl(String url, InputStream urlInputStream) throws IOException {
		InputStream in = null;
		try { 
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			in = urlInputStream==null?new URL(url).openStream():urlInputStream;
			IOUtils.copy(in, out);
			out.close();
			return out.toByteArray();
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
	public void downloadMissing() {
		FlacAlbumDao flacAlbumDao = getFlacAlbumDao();
		for (FlacAlbumBean flacAlbumBean : flacAlbumDao.getAll()) {
			if (findCoversForAlbum(flacAlbumBean).isEmpty()) {
				log.info("Downloading covers for " + flacAlbumBean.getTitle() + " by " + flacAlbumBean.getFlacArtistBean().getName());
				SortedSet<AlbumCoverBean> covers = downloadCoversForAlbum(flacAlbumBean);
				log.info("Downloaded " + covers.size() + " covers.");
			}
		}
	}
	
	@Override
	public void purgeCovers() {
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		for (AlbumCoverBean albumCoverBean : albumCoverDao.getAll()) {
			File albumCoverDirectory = new File(albumCoverBean.getFlacAlbumPath());
			if (!albumCoverDirectory.isDirectory()) {
				albumCoverDao.remove(albumCoverBean);
			}
		}
	}
	
	@Override
	@Transactional(rollbackFor=IOException.class)
	public AlbumCoverBean saveAndSelectCover(FlacAlbumBean flacAlbumBean,
			String imageUrl, InputStream urlInputStream, AlbumCoverSize albumCoverSize) throws IOException {
		AlbumCoverBean albumCoverBean = new AlbumCoverBean();
		albumCoverBean.setAlbumCoverSize(albumCoverSize);
		AlbumCoverDao albumCoverDao = getAlbumCoverDao();
		String albumPath = getPathForFlacAlbum(flacAlbumBean);
		AlbumCoverBean selectedAlbumCoverBean = albumCoverDao.findSelectedCoverForAlbumPath(albumPath);
		if (selectedAlbumCoverBean != null) {
			selectedAlbumCoverBean.setDateSelected(null);
			albumCoverDao.store(selectedAlbumCoverBean);
		}
		downloadAndStoreAlbumCover(albumCoverBean, albumPath, imageUrl, urlInputStream, true);
		return albumCoverBean;
	}
	
	@Override
	public void resizeCover(AlbumCoverBean albumCoverBean, Dimension maximumSize, String extension, OutputStream out) throws IOException {
		BufferedImage sourceImage = ImageIO.read(new ByteArrayInputStream(albumCoverBean.getCover()));
		Color transparent = new Color(0, 0, 0, 0);
		BufferedImage targetImage = getImageService().resize(sourceImage, maximumSize, transparent);
		ImageIO.write(targetImage, extension, out);
	}
	
	public AmazonService getAmazonService() {
		return i_amazonService;
	}

	@Required
	public void setAmazonService(AmazonService amazonService) {
		i_amazonService = amazonService;
	}

	public Transformer<ImageSet, AlbumCoverBean> getImageSetTransformer() {
		return i_imageSetTransformer;
	}

	@Required
	public void setImageSetTransformer(
			Transformer<ImageSet, AlbumCoverBean> imageSetTransformer) {
		i_imageSetTransformer = imageSetTransformer;
	}

	public FlacAlbumService getFlacAlbumService() {
		return i_flacAlbumService;
	}

	@Required
	public void setFlacAlbumService(FlacAlbumService flacAlbumService) {
		i_flacAlbumService = flacAlbumService;
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	@Required
	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

	public FlacAlbumDao getFlacAlbumDao() {
		return i_flacAlbumDao;
	}

	@Required
	public void setFlacAlbumDao(FlacAlbumDao flacAlbumDao) {
		i_flacAlbumDao = flacAlbumDao;
	}

	public ImageService getImageService() {
		return i_imageService;
	}

	@Required
	public void setImageService(ImageService imageService) {
		i_imageService = imageService;
	}

	public Predicate<FlacAlbumBean> getAlbumHasCoversPredicate() {
		return i_albumHasCoversPredicate;
	}

	public void setAlbumHasCoversPredicate(
			Predicate<FlacAlbumBean> albumHasCoversPredicate) {
		i_albumHasCoversPredicate = albumHasCoversPredicate;
	}

	public int getThumbnailSize() {
		return i_thumbnailSize;
	}

	public void setThumbnailSize(int thumbnailSize) {
		i_thumbnailSize = thumbnailSize;
	}

}