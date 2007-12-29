package uk.co.unclealex.music.core.service.filesystem;

public abstract class PathInformationBeanVisitor {

	public void visit(PathInformationBean pathInformationBean) {
		throw new IllegalArgumentException(pathInformationBean.getClass().getName());
	}
	
	public abstract void visit(DirectoryInformationBean directoryInformationBean);
	
	public abstract void visit(FileInformationBean fileInformationBean);
}
