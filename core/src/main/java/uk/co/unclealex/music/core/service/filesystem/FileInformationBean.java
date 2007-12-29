package uk.co.unclealex.music.core.service.filesystem;

public class FileInformationBean extends PathInformationBean {

	private int i_encodedTrackBeanId;
	
	public FileInformationBean(String path, long length, int encodedTrackBeanId) {
		super(path, length);
		i_encodedTrackBeanId = encodedTrackBeanId;
	}
	
	@Override
	public void accept(PathInformationBeanVisitor visitor) {
		visitor.visit(this);
	}

	public int getEncodedTrackBeanId() {
		return i_encodedTrackBeanId;
	}

	public void setEncodedTrackBeanId(int encodedTrackBeanId) {
		i_encodedTrackBeanId = encodedTrackBeanId;
	}
}
