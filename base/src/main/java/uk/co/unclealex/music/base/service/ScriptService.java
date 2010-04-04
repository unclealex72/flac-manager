package uk.co.unclealex.music.base.service;

import java.io.IOException;
import java.util.Map;

import uk.co.unclealex.music.base.process.service.ProcessCallback;

public interface ScriptService {

	public void runCommand(String commandName, ProcessCallback callback, Map<String, Object> model) throws ScriptException, IOException;
}
