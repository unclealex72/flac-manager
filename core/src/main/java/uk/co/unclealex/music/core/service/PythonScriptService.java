package uk.co.unclealex.music.core.service;

public class PythonScriptService extends AbstractScriptService {

	@Override
	protected String getExtension() {
		return "py";
	}

	@Override
	protected String getPathPrefix() {
		return "python";
	}

}
