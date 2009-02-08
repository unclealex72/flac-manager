package uk.co.unclealex.music.core.service.filesystem;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.OwnerBean;
import uk.co.unclealex.music.core.service.OwnerService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatServiceFactory;

@Transactional
public class EncodedRepositoryAdaptor extends AbstractKeyedRepositoryAdaptor<EncodedTrackBean> implements RepositoryAdaptor<EncodedTrackBean> {

	private EncodedTrackDao i_encodedTrackDao;
	private OwnerService i_ownerService;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private Set<String> i_titleFormats;

	@Override
	public Set<FileCommandBean> transform(EncodedTrackBean encodedTrackBean) {
		Set<FileCommandBean> fileBeans = new HashSet<FileCommandBean>();		
		OwnerService ownerService = getOwnerService();
		TitleFormatServiceFactory factory = getTitleFormatServiceFactory();
		for (String titleFormat : getTitleFormats()) {
			TitleFormatService titleFormatService = factory.createTitleFormatService(titleFormat);
			if (titleFormatService.isOwnerRequired()) {
				for (OwnerBean ownerBean : ownerService.getOwners(encodedTrackBean.getEncodedAlbumBean())) {
					fileBeans.add(
						createFileBean(
							titleFormatService.getTitle(encodedTrackBean, ownerBean), encodedTrackBean));
				}					
			}
			else {
				fileBeans.add(
					createFileBean(
						titleFormatService.getTitle(encodedTrackBean), encodedTrackBean));				
			}
		}
		return fileBeans;
	}

	protected FileCommandBean createFileBean(
			final String title, EncodedTrackBean encodedTrackBean) {
		return new FileCommandBean(
			encodedTrackBean.getId(), title, new Date(encodedTrackBean.getTimestamp()), 
			encodedTrackBean.getTrackData().getLength(), encodedTrackBean.getEncoderBean().getContentType()); 
	}

	@Override
	public KeyedReadOnlyDao<EncodedTrackBean> getDao() {
		return getEncodedTrackDao();
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

	@Required
	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	@Required
	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public Set<String> getTitleFormats() {
		return i_titleFormats;
	}

	@Required
	public void setTitleFormats(Set<String> titleFormats) {
		i_titleFormats = titleFormats;
	}
}
