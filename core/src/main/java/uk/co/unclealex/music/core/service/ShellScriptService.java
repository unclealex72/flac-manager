package uk.co.unclealex.music.core.service;

public class ShellScriptService extends AbstractScriptService {

	@Override
	protected String getExtension() {
		return "sh";
	}

	@Override
	protected String getPathPrefix() {
		return "shell";
	}

}
