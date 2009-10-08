package uk.co.unclealex.music.web.commands;

import java.io.IOException;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.commands.Command;
import uk.co.unclealex.music.encoder.exception.AlreadyEncodingException;
import uk.co.unclealex.music.encoder.exception.CurrentlyScanningException;
import uk.co.unclealex.music.encoder.exception.MultipleEncodingException;
import uk.co.unclealex.music.encoder.service.EncoderResultBean;
import uk.co.unclealex.music.encoder.service.EncoderService;

public class Encode implements Command {

	private RepositoryManager i_encodedRepositoryManager;
	private ClearableRepositoryFactory i_encodedRepositoryFactory;
	private RepositoryManager i_coversRepositoryManager;
	private ClearableRepositoryFactory i_coversRepositoryFactory;
	private EncoderService i_encoderService;
	private AlbumCoverService i_albumCoverService;
	
	@Override
	public void execute(String[] args) throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException, RepositoryException {
		EncoderResultBean encoderResultBean = getEncoderService().encodeAllAndRemoveDeleted();
		Set<FlacAlbumBean> flacAlbumBeans = encoderResultBean.getFlacAlbumBeans();
		AlbumCoverService albumCoverService = getAlbumCoverService();
		albumCoverService.downloadAndSaveCoversForAlbums(flacAlbumBeans);

		getEncodedRepositoryFactory().clearNextInstance();
		getEncodedRepositoryManager().updateFromScratch();
		getCoversRepositoryFactory().clearNextInstance();
		getCoversRepositoryManager().updateFromScratch();
	}
	
	public RepositoryManager getEncodedRepositoryManager() {
		return i_encodedRepositoryManager;
	}

	@Required
	public void setEncodedRepositoryManager(RepositoryManager encodedRepositoryManager) {
		i_encodedRepositoryManager = encodedRepositoryManager;
	}

	public ClearableRepositoryFactory getEncodedRepositoryFactory() {
		return i_encodedRepositoryFactory;
	}

	@Required
	public void setEncodedRepositoryFactory(ClearableRepositoryFactory encodedRepositoryFactory) {
		i_encodedRepositoryFactory = encodedRepositoryFactory;
	}

	public RepositoryManager getCoversRepositoryManager() {
		return i_coversRepositoryManager;
	}

	@Required
	public void setCoversRepositoryManager(RepositoryManager coversRepositoryManager) {
		i_coversRepositoryManager = coversRepositoryManager;
	}

	public ClearableRepositoryFactory getCoversRepositoryFactory() {
		return i_coversRepositoryFactory;
	}

	@Required
	public void setCoversRepositoryFactory(ClearableRepositoryFactory coversRepositoryFactory) {
		i_coversRepositoryFactory = coversRepositoryFactory;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	@Required
	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	@Required
	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}
}
