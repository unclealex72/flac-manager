package uk.co.unclealex.music.base.service.filesystem;

import java.util.Collection;
import java.util.Set;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FileBean;
import uk.co.unclealex.music.base.model.OwnerBean;

public interface FileSystemService {

	public Set<FileBean> addFiles(EncodedTrackBean encodedTrackBean) throws WrongFileTypeException;
	
	public Set<String> removeFiles(EncodedTrackBean encodedTrackBean) throws WrongFileTypeException;

	public Set<FileBean> addFiles(Collection<OwnerBean> ownerBeans, EncodedTrackBean encodedTrackBean) throws WrongFileTypeException;

	public Set<String> removeFiles(Collection<OwnerBean> ownerBeans, EncodedTrackBean encodedTrackBean) throws WrongFileTypeException;
}
