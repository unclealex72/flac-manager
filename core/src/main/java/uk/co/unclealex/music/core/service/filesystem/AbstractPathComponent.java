package uk.co.unclealex.music.core.service.filesystem;

public abstract class AbstractPathComponent implements PathComponent {

	private Context i_context;

	public Context getContext() {
		return i_context;
	}

	public void setContext(Context context) {
		i_context = context;
	}
}
