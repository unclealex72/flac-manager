package uk.co.unclealex.music.core.service.filesystem;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.collections15.ComparatorUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.music.core.util.Tree;
import uk.co.unclealex.music.core.util.TreeSetTree;

@Service
@Transactional
public class FileSystemCacheImpl implements FileSystemCache {

	private SortedMap<String, Tree<PathInformationBean>> i_nodesByPath; 
	private EncodedTrackDao i_encodedTrackDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	
	@Override
	public void createCache(Collection<String> titleFormats) {
		TreeMap<String, Tree<PathInformationBean>> nodesByPath = 
			new TreeMap<String, Tree<PathInformationBean>>();
		setNodesByPath(nodesByPath);
		TitleFormatServiceFactory factory = getTitleFormatServiceFactory();
		SortedSet<EncodedTrackBean> encodedTrackBeans = getEncodedTrackDao().getAll();
		for (String titleFormat : titleFormats) {
			TitleFormatService titleFormatService = factory.createTitleFormatService(titleFormat);
			for (EncodedTrackBean encodedTrackBean : encodedTrackBeans) {
				addToCache(titleFormatService.getTitle(encodedTrackBean), encodedTrackBean, nodesByPath);
			}
		}
		if (!nodesByPath.isEmpty()) {
			updateTimestamps(nodesByPath.get(""));
		}
	}

	protected Tree<PathInformationBean> addToCache(
			final String title, EncodedTrackBean encodedTrackBean, TreeMap<String, Tree<PathInformationBean>> nodesByPath) {
		TreeSetTree<PathInformationBean> node;
		if (title.isEmpty()) {
			DirectoryInformationBean rootDirectory = new DirectoryInformationBean(title);
			node = new TreeSetTree<PathInformationBean>(rootDirectory);
		}
		else {
			PathInformationBean pathInformationBean;
			if (encodedTrackBean != null) {
				pathInformationBean = new FileInformationBean(title, encodedTrackBean.getLength(), encodedTrackBean.getId());
				Date date = new Date(encodedTrackBean.getTimestamp());
				pathInformationBean.setCreationDate(date);
				pathInformationBean.setLastModifiedDate(date);
			}
			else {
				pathInformationBean = new DirectoryInformationBean(title);
			}
			node = new TreeSetTree<PathInformationBean>(pathInformationBean);
			final int lastSlashPos = title.lastIndexOf('/');
			String parentTitle = lastSlashPos==-1?"":title.substring(0, lastSlashPos);
			Tree<PathInformationBean> parent = nodesByPath.get(parentTitle);
			if (parent == null) {
				parent = addToCache(parentTitle, null, nodesByPath);
			}
			parent.getChildren().add(node);
			PathInformationBeanVisitor visitor = new PathInformationBeanVisitor() {
				@Override
				public void visit(DirectoryInformationBean directoryInformationBean) {
					String childName = title.substring(lastSlashPos + 1);
					directoryInformationBean.getChildren().add(childName);
				}
				@Override
				public void visit(FileInformationBean fileInformationBean) {
					throw new IllegalStateException(
							fileInformationBean.getPath() + " is in the cache as a file and not as a directory.");
				}
			};
			parent.getValue().accept(visitor);
		}
		nodesByPath.put(title, node);
		return node;
	}

	protected PathInformationBean updateTimestamps(Tree<PathInformationBean> tree) {
		Set<Tree<PathInformationBean>> children = tree.getChildren();
		PathInformationBean pathInformationBean = tree.getValue();
		if (children.isEmpty()) {
			return pathInformationBean;
		}
		Date creationDate = null, lastModifiedDate = null;
		for (Tree<PathInformationBean> child : children) {
			PathInformationBean childInfo = updateTimestamps(child);
			creationDate = earliest(creationDate, childInfo.getCreationDate());
			lastModifiedDate = earliest(lastModifiedDate, childInfo.getLastModifiedDate());
		}
		pathInformationBean.setCreationDate(creationDate);
		pathInformationBean.setLastModifiedDate(creationDate);
		return pathInformationBean;
	}
	
	@SuppressWarnings("unchecked")
	protected Date earliest(Date d1, Date d2) {
		Comparator<Date> comparator = ComparatorUtils.nullHighComparator(ComparatorUtils.NATURAL_COMPARATOR);
		return comparator.compare(d1, d2) < 0?d1:d2;
	}
	
	@Override
	public PathInformationBean findPath(String path) {
		Tree<PathInformationBean> node = getNodesByPath().get(path);
		return node==null?null:node.getValue();
	}

	public SortedMap<String, Tree<PathInformationBean>> getNodesByPath() {
		return i_nodesByPath;
	}

	public void setNodesByPath(
			SortedMap<String, Tree<PathInformationBean>> nodesByPath) {
		i_nodesByPath = nodesByPath;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

}
