package uk.co.unclealex.music.web.webdav;

public class WebdavStoreAdapter extends
		org.apache.slide.simple.store.WebdavStoreAdapter {

	protected Integer i_scopeLength;

	public int getScopeLength() {
		if (i_scopeLength == null) {
			setScopeLength(scope.toString().length());
		}
		return i_scopeLength;
	}

	public void setScopeLength(int scopeLength) {
		i_scopeLength = scopeLength;
	}
}
