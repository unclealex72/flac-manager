package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.SlimServerConfig;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.OwnerService;

@Transactional
public class OwnerServiceImpl implements OwnerService {

	private static final Logger log = Logger.getLogger(OwnerServiceImpl.class);
	private OwnerDao i_ownerDao;
	private FlacTrackDao i_flacTrackDao;
	private SlimServerConfig i_slimServerConfig;
	
	@Override
	public SortedMap<OwnerBean, SortedSet<FlacTrackBean>> resolveOwnershipByFiles() {
		File rootDirectory = new File(getSlimServerConfig().getRootDirectory());
		final Map<String, FlacTrackBean> tracksByPath = new HashMap<String, FlacTrackBean>();
		for (FlacTrackBean flacTrackBean : getFlacTrackDao().getAll()) {
			try {
				File flacFile = new File(new URI(flacTrackBean.getUrl()));
				tracksByPath.put(flacFile.getAbsolutePath(), flacTrackBean);
			}
			catch (URISyntaxException e) {
				log.warn("Invalid URI found: " + flacTrackBean.getUrl());
			}
		}
		final SortedMap<OwnerBean, SortedSet<FlacTrackBean>> flacTrackBeansByOwner = new TreeMap<OwnerBean, SortedSet<FlacTrackBean>>();
		final SortedMap<String, OwnerBean> ownerBeansByLowerCaseName = new TreeMap<String, OwnerBean>();
		for (OwnerBean ownerBean : getOwnerDao().getAll()) {
			ownerBeansByLowerCaseName.put(ownerBean.getName().toLowerCase(), ownerBean);
		}
		Transformer<String, String> literalOwnerNameTransformer = new Transformer<String, String>() {
			@Override
			public String transform(String ownerName) {
				return Pattern.quote(ownerName);
			}
		};
		Collection<String> literalOwnerNames = CollectionUtils.collect(ownerBeansByLowerCaseName.keySet(), literalOwnerNameTransformer);
		final Pattern filenamePattern = Pattern.compile("owner\\.(" + StringUtils.join(literalOwnerNames, '|') + ")", Pattern.CASE_INSENSITIVE);
		FilenameFilter ownerFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				Matcher matcher = filenamePattern.matcher(name);
				if (matcher.matches()) {
					String ownerName = matcher.group(1);
					OwnerBean ownerBean = ownerBeansByLowerCaseName.get(ownerName.toLowerCase());
					SortedSet<FlacTrackBean> flacTrackBeans = flacTrackBeansByOwner.get(ownerBean);
					if (flacTrackBeans == null) {
						flacTrackBeans = new TreeSet<FlacTrackBean>();
						flacTrackBeansByOwner.put(ownerBean, flacTrackBeans);
					}
					String directoryUrl = dir.getAbsolutePath() + "/";
					for (Map.Entry<String, FlacTrackBean> entry : tracksByPath.entrySet()) {
						if (entry.getKey().startsWith(directoryUrl)) {
							flacTrackBeans.add(entry.getValue());
						}
					}
				}
				return false;
			}
		};
		FileUtils.listFiles(rootDirectory, FileFilterUtils.asFileFilter(ownerFilter), FileFilterUtils.trueFileFilter());
		return flacTrackBeansByOwner;
	}
	
	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	@Required
	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	public SlimServerConfig getSlimServerConfig() {
		return i_slimServerConfig;
	}

	public void setSlimServerConfig(SlimServerConfig slimServerConfig) {
		i_slimServerConfig = slimServerConfig;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
