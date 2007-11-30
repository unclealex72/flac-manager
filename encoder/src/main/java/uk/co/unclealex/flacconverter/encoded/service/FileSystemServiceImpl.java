package uk.co.unclealex.flacconverter.encoded.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;
import org.hibernate.SessionFactory;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatService;
import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.spring.HibernateSessionBinder;
import uk.co.unclealex.flacconverter.util.Tree;
import uk.co.unclealex.flacconverter.util.TreeSetTree;

public class FileSystemServiceImpl implements FileSystemService, EncodingEventListener {

	private Tree<String> i_hierarchy;
	private BidiMap<Tree<String>, EncodedTrackBean> i_encodedTrackBeansByPath;
	private EncoderService i_encoderService;
	private EncodedTrackDao i_encodedTrackDao;
	private FlacTrackDao i_flacTrackDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private String i_titleFormat;
	private TitleFormatService i_titleFormatService;
	private SessionFactory i_sessionFactory;
	
	public void initialise() {
		setTitleFormatService(
				getTitleFormatServiceFactory().createTitleFormatService(getTitleFormat()));
		setHierarchy(new TreeSetTree<String>(null));
		setEncodedTrackBeansByPath(new DualHashBidiMap<Tree<String>, EncodedTrackBean>());
		getEncoderService().registerEncodingEventListener(this);
		new Thread() {
			@Override
			public void run() {
				HibernateSessionBinder binder = new HibernateSessionBinder(getSessionFactory());
				binder.bind();
				for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
					FlacTrackBean flacTrackBean = getFlacTrackDao().findByUrl(encodedTrackBean.getFlacUrl()); 
					addToHierarchy(flacTrackBean, encodedTrackBean.getEncoderBean(), encodedTrackBean);
				}
				binder.unbind();
			}
		}.start();
	}
	
	@Override
	public void afterTrackEncoded(EncodedTrackBean encodedTrackBean, FlacTrackBean flacTrackBean) {
		addToHierarchy(flacTrackBean, encodedTrackBean.getEncoderBean(), encodedTrackBean);
	}
	
	@Override
	public void beforeTrackRemoved(EncodedTrackBean encodedTrackBean) {
		Tree<String> tree = getEncodedTrackBeansByPath().getKey(encodedTrackBean);
		if (tree != null) {
			tree.removeUpwards();
		}
	}
	
	protected void addToHierarchy(FlacTrackBean flacTrackBean, EncoderBean encoderBean, EncodedTrackBean encodedTrackBean) {
		String title = getTitleFormatService().getTitle(flacTrackBean, encoderBean);
		Tree<String> tree = getHierarchy().createPath(split(title).iterator());
		getEncodedTrackBeansByPath().put(tree, encodedTrackBean);
	}

	protected Tree<String> findTreeForPath(String path) {
		return getHierarchy().traverse(split(path).iterator());
	}
	@Override
	public boolean exists(String path) {
		Tree<String> tree = findTreeForPath(path);
		if (tree == null) {
			return false;
		}
		if (!doesPathEndWithSeparator(path)) {
			return true;
		}
		return getEncodedTrackBeansByPath().get(tree) == null;
	}

	@Override
	public String[] getChildPaths(String directoryPath) {
		Tree<String> tree = findTreeForPath(directoryPath);
		if (tree == null) {
			return null;
		}
		SortedSet<String> childPaths = new TreeSet<String>();
		for (Tree<String> child : tree.getChildren()) {
			childPaths.add(child.getValue());
		}
		return childPaths.toArray(new String[childPaths.size()]);
	}

	@Override
	public boolean isDirectory(String path) {
		Tree<String> tree = findTreeForPath(path);
		return tree != null && tree.getChildren().size() != 0;
	}

	@Override
	public EncodedTrackBean getEncodedTrackBean(String path) {
		if (doesPathEndWithSeparator(path)) {
			return null;
		}
		Tree<String> tree = findTreeForPath(path);
		if (tree == null) {
			return null;
		}
		return getEncodedTrackBeansByPath().get(tree);
	}

	protected boolean doesPathEndWithSeparator(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		char lastChar = path.charAt(path.length() - 1);
		return (lastChar == IOUtils.DIR_SEPARATOR_UNIX || lastChar == IOUtils.DIR_SEPARATOR_WINDOWS);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> split(String pathName) {
		StrMatcher matcher = 
			StrMatcher.charSetMatcher(new char[] { IOUtils.DIR_SEPARATOR_UNIX, IOUtils.DIR_SEPARATOR_WINDOWS });
		StrTokenizer tokenizer = new StrTokenizer(pathName, matcher);
		List<String> tokens = tokenizer.getTokenList();
		// Ignore a leading slash
		if (!tokens.isEmpty() && tokens.get(0).isEmpty()) {
			tokens.remove(0);
		}
		return tokens;
	}
	public Tree<String> getHierarchy() {
		return i_hierarchy;
	}

	public void setHierarchy(Tree<String> hierarchy) {
		i_hierarchy = hierarchy;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public BidiMap<Tree<String>, EncodedTrackBean> getEncodedTrackBeansByPath() {
		return i_encodedTrackBeansByPath;
	}

	public void setEncodedTrackBeansByPath(
			BidiMap<Tree<String>, EncodedTrackBean> encodedTrackBeansByPath) {
		i_encodedTrackBeansByPath = encodedTrackBeansByPath;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

	public String getTitleFormat() {
		return i_titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		i_titleFormat = titleFormat;
	}

	public TitleFormatService getTitleFormatService() {
		return i_titleFormatService;
	}

	public void setTitleFormatService(TitleFormatService titleFormatService) {
		i_titleFormatService = titleFormatService;
	}

	public SessionFactory getSessionFactory() {
		return i_sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		i_sessionFactory = sessionFactory;
	}

}
