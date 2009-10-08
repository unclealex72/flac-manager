package uk.co.unclealex.music.encoder.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.filesystem.FileSystemService;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.FileAddedAction;
import uk.co.unclealex.music.encoder.action.FileRemovedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

@Transactional
public class FileSystemEncodingEventListener extends AbstractEncodingEventListener {

	private FileSystemService i_fileSystemService;
	
	@Override
	public void trackAdded(FlacTrackBean flacTrackBean, final EncodedTrackBean encodedTrackBean, final List<EncodingAction> encodingActions) throws EventException {
		AddingFileSystemServiceCallback callback = new AddingFileSystemServiceCallback() {
			@Override
			public Collection<FileBean> doInFileSystemService(FileSystemService fileSystemService) throws Exception {
				return fileSystemService.addFiles(encodedTrackBean);
			}
		};
		execute(callback, encodedTrackBean, encodingActions);
	}

	@Override
	public void trackRemoved(
			final EncodedTrackBean encodedTrackBean, final List<EncodingAction> encodingActions) throws EventException {
		RemovingFileSystemServiceCallback callback = new RemovingFileSystemServiceCallback() {
			@Override
			public Collection<String> doInFileSystemService(FileSystemService fileSystemService) throws Exception {
				return fileSystemService.removeFiles(encodedTrackBean);
			}
		};
		execute(callback, encodedTrackBean, encodingActions);
	}

	@Override
	public void ownerAdded(final OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, final List<EncodingAction> encodingActions)
			throws EventException {
		AddingFileSystemServiceCallback callback = new AddingFileSystemServiceCallback() {
			@Override
			public Collection<FileBean> doInFileSystemService(FileSystemService fileSystemService) throws Exception {
				return fileSystemService.addFiles(Collections.singleton(ownerBean), encodedTrackBean);
			}
		};
		execute(callback, encodedTrackBean, encodingActions);
	}

	@Override
	public void ownerRemoved(
			final OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions) throws EventException {
		RemovingFileSystemServiceCallback callback = new RemovingFileSystemServiceCallback() {
			@Override
			public Collection<String> doInFileSystemService(FileSystemService fileSystemService) throws Exception {
				return fileSystemService.removeFiles(Collections.singleton(ownerBean), encodedTrackBean);
			}
		};
		execute(callback, encodedTrackBean, encodingActions);
	}
	
	protected interface FileSystemServiceCallback<A> {
		
		public Collection<A> doInFileSystemService(FileSystemService fileSystemService) throws Exception;
		
		public EncodingAction createEncodingAction(A artifact, EncodedTrackBean encodedTrackBean);
	}
	
	protected abstract class AddingFileSystemServiceCallback implements FileSystemServiceCallback<FileBean> {
		@Override
		public EncodingAction createEncodingAction(FileBean fileBean, EncodedTrackBean encodedTrackBean) {
			return new FileAddedAction(fileBean.getPath(), encodedTrackBean);
		}
	}
	
	protected abstract class RemovingFileSystemServiceCallback implements FileSystemServiceCallback<String> {
		@Override
		public EncodingAction createEncodingAction(String path, EncodedTrackBean encodedTrackBean) {
			return new FileRemovedAction(path, encodedTrackBean);
		}
	}
	
	protected <A> void execute(
			final FileSystemServiceCallback<A> fileSystemServiceCallback, final EncodedTrackBean encodedTrackBean,
			List<EncodingAction> encodingActions) throws EventException {
		try {
			Collection<A> files = fileSystemServiceCallback.doInFileSystemService(getFileSystemService());
			Transformer<A, EncodingAction> transformer = new Transformer<A, EncodingAction>() {
				public EncodingAction transform(A artifact) {
					return fileSystemServiceCallback.createEncodingAction(artifact, encodedTrackBean);
				};
			};
			CollectionUtils.collect(files, transformer, encodingActions);
		}
		catch (Exception e) {
			throw new EventException(e);
		}
	}
	
	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}
}
