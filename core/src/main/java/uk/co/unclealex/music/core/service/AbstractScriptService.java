package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;

import uk.co.unclealex.music.base.process.service.ProcessCallback;
import uk.co.unclealex.music.base.process.service.ProcessResult;
import uk.co.unclealex.music.base.process.service.ProcessService;
import uk.co.unclealex.music.base.service.ScriptException;
import uk.co.unclealex.music.base.service.ScriptService;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractScriptService implements ScriptService {

	private Configuration i_configuration;
	private ProcessService i_processService;
	
	@PostConstruct
	public void initialise() {
		Configuration configuration = new Configuration();
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		configuration.setClassForTemplateLoading(getClass(), getPathPrefix());
		setConfiguration(configuration);
	}
	
	@Override
	public void runCommand(String commandName, ProcessCallback callback, Map<String, Object> model) throws IOException, ScriptException {
		String extension = getExtension();
		String templateName = String.format("%s.%s.ftl", commandName, extension);
		File pythonFile = null;
		Writer out = null;
		try {
			Template template = getConfiguration().getTemplate(templateName);
			pythonFile = File.createTempFile("scriptService-", "." + extension);
			pythonFile.setExecutable(true, true);
			pythonFile.deleteOnExit();
			out = new FileWriter(pythonFile);
			template.process(model, out);
			out.close();
			runCommand(pythonFile, callback);
		}
		catch (TemplateException e) {
			throw new ScriptRuntimeException(
					"An unexpected error occurred using python command " + commandName + " and file " + pythonFile.getAbsolutePath(), e);
		}
		finally {
			IOUtils.closeQuietly(out);
			if (pythonFile != null) {
				pythonFile.delete();
			}
		}
	}

	protected void runCommand(File pythonFile, final ProcessCallback processCallback) throws IOException, ScriptException {
		ProcessBuilder processBuilder = new ProcessBuilder(pythonFile.getAbsolutePath());
		ProcessResult processResult = getProcessService().run(processBuilder, processCallback, false);
		if (processResult.getReturnValue() != 0) {
			throw new ScriptException(processResult.getError());
		}
	}

	protected abstract String getExtension();
	
	protected abstract String getPathPrefix();

	public Configuration getConfiguration() {
		return i_configuration;
	}

	public void setConfiguration(Configuration configuration) {
		i_configuration = configuration;
	}

	public ProcessService getProcessService() {
		return i_processService;
	}

	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}
}
