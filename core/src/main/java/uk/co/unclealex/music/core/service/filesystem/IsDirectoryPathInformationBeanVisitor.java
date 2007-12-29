package uk.co.unclealex.music.core.service.filesystem;

public class IsDirectoryPathInformationBeanVisitor extends
		ValueReturningPathInformationBeanVisitor<Boolean> {

	@Override
	public void visit(DirectoryInformationBean directoryInformationBean) {
		setValue(true);
	}

	@Override
	public void visit(FileInformationBean fileInformationBean) {
		setValue(false);
	}
}
