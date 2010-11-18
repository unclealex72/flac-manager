package uk.co.unclealex.music.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import uk.co.unclealex.music.Device;

public abstract class ProcessSynchroniser<D extends Device> extends AbstractSynchroniser<D> {

	private Process i_process;
	private PrintWriter i_stdin;
	private BufferedReader i_stdout;
	private Future<?> i_stderrFuture;
	private File i_commandFile;
	
	@Override
	protected void initialiseDevice() throws IOException {
		List<String> command = new ArrayList<String>();
		File commandFile = File.createTempFile("sync-", "");
		setCommandFile(commandFile);
		commandFile.deleteOnExit();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = getCommandAsStream();
			out = new FileOutputStream(commandFile);
			IOUtils.copy(in, out);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		commandFile.setExecutable(true);
		command.add(commandFile.getCanonicalPath());
		command.addAll(Arrays.asList(getCommandArguments()));
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		setProcess(process);
		setStdin(new PrintWriter(process.getOutputStream()));
		setStdout(new BufferedReader(new InputStreamReader(process.getInputStream())));
		final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String line;
				try {
					while ((line = stderr.readLine()) != null) {
						log.info("PROCESS: " + line);
					}
				}
				catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				finally {
					IOUtils.closeQuietly(stderr);
				}
			}
		};
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> stderrFuture = executor.submit(runnable);
		setStderrFuture(stderrFuture);
	}

	protected abstract InputStream getCommandAsStream() throws IOException;

	protected abstract String[] getCommandArguments();

	@Override
	protected Set<DeviceFile> listDeviceFiles() throws IOException {
		DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinuteSecond();
		List<String> deviceFileStrings = executeCommand("LIST");
		Set<DeviceFile> deviceFiles = new TreeSet<DeviceFile>();
		for (String deviceFileString : deviceFileStrings) {
			String[] deviceFileParts = StringUtils.split(deviceFileString, '|');
			deviceFiles.add(
				new DeviceFile(deviceFileParts[0], deviceFileParts[1], formatter.parseDateTime(deviceFileParts[2]).getMillis()));
		}
		return deviceFiles;
	}	

	@Override
	protected void add(LocalFile localFile) throws IOException {
		executeCommand("ADD", localFile.getRelativePath(), localFile.getFile().getCanonicalPath());
	}

	@Override
	protected void remove(DeviceFile deviceFile) throws IOException {
		executeCommand("REMOVE", deviceFile.getId());
	}

	@Override
	protected void closeDevice() throws IOException {
		try {
			executeCommand("QUIT");
		}
		finally {
			IOUtils.closeQuietly(getStdin());
			IOUtils.closeQuietly(getStdout());
			try {
				getStderrFuture().get(1, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
				log.error("stderr logging did not close properly.", e);
			}
			catch (ExecutionException e) {
				log.error("stderr logging did not close properly.", e);
			}
			catch (TimeoutException e) {
				log.error("stderr logging did not close properly.", e);
			}
			getProcess().destroy();
			getCommandFile().delete();
		}
	}

	protected List<String> executeCommand(String... command) throws IOException {
		String fullCommand = StringUtils.join(command, '|');
		getStdin().println(fullCommand);
		getStdin().flush();
		List<String> results = new ArrayList<String>();
		String line;
		boolean commandFinished = false;
		while (!commandFinished && ((line = getStdout().readLine()) != null)) {
			line = StringUtils.trimToEmpty(line);
			if ("OK".equals(line)) {
				commandFinished = true;
			}
			else {
				results.add(line);
			}
		}
		if (!commandFinished) {
			throw new IOException("Command " + fullCommand + " failed");
		}
		return results;
	}

	public Process getProcess() {
		return i_process;
	}

	public void setProcess(Process process) {
		i_process = process;
	}

	public PrintWriter getStdin() {
		return i_stdin;
	}

	public void setStdin(PrintWriter stdin) {
		i_stdin = stdin;
	}

	public BufferedReader getStdout() {
		return i_stdout;
	}

	public void setStdout(BufferedReader stdout) {
		i_stdout = stdout;
	}

	public Future<?> getStderrFuture() {
		return i_stderrFuture;
	}

	public void setStderrFuture(Future<?> stderrFuture) {
		i_stderrFuture = stderrFuture;
	}

	public File getCommandFile() {
		return i_commandFile;
	}

	public void setCommandFile(File commandFile) {
		i_commandFile = commandFile;
	}
}