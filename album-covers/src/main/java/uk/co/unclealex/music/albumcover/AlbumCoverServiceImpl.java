package uk.co.unclealex.music.albumcover;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.Holder;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.albumcover.model.AlbumCoverBean;
import uk.co.unclealex.music.albumcover.model.AlbumCoverSize;
import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacArtistBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;

import com.amazon.webservices.awsecommerceservice._2008_10_06.Errors;
import com.amazon.webservices.awsecommerceservice._2008_10_06.ImageSet;
import com.amazon.webservices.awsecommerceservice._2008_10_06.Item;
import com.amazon.webservices.awsecommerceservice._2008_10_06.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2008_10_06.Items;
import com.amazon.webservices.awsecommerceservice._2008_10_06.OperationRequest;
import com.amazon.webservices.awsecommerceservice._2008_10_06.Errors.Error;
import com.amazon.webservices.awsecommerceservice._2008_10_06.Item.ImageSets;

@Service
public class AlbumCoverServiceImpl implements AlbumCoverService {

	private static final Logger log = Logger.getLogger(AlbumCoverServiceImpl.class);
	
	private AmazonService i_amazonService;
	private Transformer<ImageSet, AlbumCoverBean> i_imageSetTransformer;
	
	public static void main(String[] args) throws IOException {
		AlbumCoverServiceImpl albumCoverServiceImpl = new AlbumCoverServiceImpl();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-music-album-covers.xml");
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(
				albumCoverServiceImpl, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
		FlacArtistBean metallica = new FlacArtistBean();
		metallica.setRawName("Metallica".getBytes());
		FlacAlbumBean deathMagnetic = new FlacAlbumBean();
		deathMagnetic.setRawTitle("Death Magnetic".getBytes());
		deathMagnetic.setFlacArtistBean(metallica);
		FlacTrackBean track1 = new FlacTrackBean();
		track1.setUrl("file:///mnt/multimedia/flac/metallica/death_magnetic/01 - Thingy.flac");
		deathMagnetic.setFlacTrackBeans(new TreeSet<FlacTrackBean>(Collections.singleton(track1)));
		albumCoverServiceImpl.downloadCoversForAlbum(deathMagnetic);
	}
	
	@Override
	public SortedSet<AlbumCoverBean> findCoversForAlbum(FlacAlbumBean flacAlbumBean) {
		
		final Map<AlbumCoverBean, Integer> albumCoverPositions = new HashMap<AlbumCoverBean, Integer>();
		int position = 0;
		
		Comparator<AlbumCoverBean> comparator = createAlbumCoverComparator(albumCoverPositions);
		
		Set<AlbumCoverBean> albumCoverBeans = new HashSet<AlbumCoverBean>();
		Transformer<ImageSet, AlbumCoverBean> imageSetTransformer = getImageSetTransformer();
		
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
							if (albumCoverBeans.add(albumCoverBean));
							log.info("Found " + albumCoverBean);
							albumCoverPositions.put(albumCoverBean, position++);
						}
					}
				}
				
			}
		}
		SortedSet<AlbumCoverBean> sortedCovers = new TreeSet<AlbumCoverBean>(comparator);
		sortedCovers.addAll(albumCoverBeans);
		return sortedCovers;
	}

	public SortedSet<AlbumCoverBean> downloadCoversForAlbum(FlacAlbumBean flacAlbumBean) throws IOException {
		File flacAlbumDirectory = getFlacAlbumDirectory(flacAlbumBean);
		if (flacAlbumDirectory == null) {
			log.info("Could not determine a directory to download to, so returning.");
			return new TreeSet<AlbumCoverBean>();
		}
		SortedSet<AlbumCoverBean> downloadedCoversForAlbum = findCoversForAlbum(flacAlbumBean);
		int position = 0;
		for (AlbumCoverBean albumCoverBean : downloadedCoversForAlbum) {
			URL url = albumCoverBean.getUrl();
			String extension = FilenameUtils.getExtension(url.toString());
			String filename = 
				String.format("cover-%03d-%s.%s", position++, albumCoverBean.getAlbumCoverSize().toString(), extension);
			File file = new File(flacAlbumDirectory, filename);
			log.info("Copying " + url + " to " + file);
			InputStream in = null;
			OutputStream out = null;
			try {
				in = url.openStream();
				out = new FileOutputStream(file);
				IOUtils.copy(in, out);
			}
			finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
		return getDownloadedCoversForAlbum(flacAlbumBean);
	}
	
	@Override
	public SortedSet<AlbumCoverBean> getDownloadedCoversForAlbum(FlacAlbumBean flacAlbumBean) {
		final Map<AlbumCoverBean, Integer> albumCoverPositions = new HashMap<AlbumCoverBean, Integer>();
		final SortedSet<AlbumCoverBean> covers = new TreeSet<AlbumCoverBean>(createAlbumCoverComparator(albumCoverPositions));
		
		File directory = getFlacAlbumDirectory(flacAlbumBean);
		if (directory != null) {
			final Pattern coverPattern = 
				Pattern.compile("cover-([0-9]+)-(" + StringUtils.join(AlbumCoverSize.values(), "|") + ")\\.(gif|jpg|png)");
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					Matcher matcher = coverPattern.matcher(name);
					if (matcher.matches()) {
						Integer position = Integer.parseInt(matcher.group(1));
						AlbumCoverSize albumCoverSize = AlbumCoverSize.valueOf(matcher.group(2));
						File file = new File(dir, name);
						AlbumCoverBean albumCoverBean;
						try {
							albumCoverBean = new AlbumCoverBean(file.toURI().toURL(), albumCoverSize);
							albumCoverPositions.put(albumCoverBean, position);
							covers.add(albumCoverBean);
						} catch (MalformedURLException e) {
							log.warn("Could not convert file " + file + " to a URL");
						}
					}
					return false;
				}
			};
			directory.list(filter);
		}
		return covers;
	}

	protected File getFlacAlbumDirectory(FlacAlbumBean flacAlbumBean) {
		File directory;
		SortedSet<FlacTrackBean> flacTrackBeans = flacAlbumBean.getFlacTrackBeans();
		if (flacTrackBeans != null && !flacTrackBeans.isEmpty()) {
			FlacTrackBean first = flacTrackBeans.first();
			directory = first.getFile().getParentFile();
		}
		else {
			directory = null;
		}
		return directory;
	}

	protected Comparator<AlbumCoverBean> createAlbumCoverComparator(Map<AlbumCoverBean, Integer> albumCoverPositions) {
		return createAlbumCoverComparator(TransformerUtils.mapTransformer(albumCoverPositions));
	}
	
	protected Comparator<AlbumCoverBean> createAlbumCoverComparator(
			final Transformer<AlbumCoverBean, Integer> albumCoverPositionTransformer) {
		Comparator<AlbumCoverBean> comparator = new Comparator<AlbumCoverBean>() {
			@Override
			public int compare(AlbumCoverBean o1, AlbumCoverBean o2) {
				int cmp = o1.getAlbumCoverSize().compareTo(o2.getAlbumCoverSize());
				return cmp != 0?
						cmp:
						albumCoverPositionTransformer.transform(o1).compareTo(albumCoverPositionTransformer.transform(o2));
			}
		};
		return comparator;
	}

	public AmazonService getAmazonService() {
		return i_amazonService;
	}

	public void setAmazonService(AmazonService amazonService) {
		i_amazonService = amazonService;
	}

	public Transformer<ImageSet, AlbumCoverBean> getImageSetTransformer() {
		return i_imageSetTransformer;
	}

	public void setImageSetTransformer(
			Transformer<ImageSet, AlbumCoverBean> imageSetTransformer) {
		i_imageSetTransformer = imageSetTransformer;
	}

}
