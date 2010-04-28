package uk.co.unclealex.music.gwt.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ArtworkServlet extends HttpServlet implements FileFilter {

	private File i_flacDirectory;
	private byte[] i_noCoverData;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		WebApplicationContext ctxt = 
			WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		File flacDirectory = ctxt.getBean("flacDirectory", File.class);
		setFlacDirectory(flacDirectory);
		InputStream in = getClass().getResourceAsStream("nocover.png");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			IOUtils.copy(in, out);
			in.close();
		}
		catch (IOException e) {
			throw new ServletException(e);
		}
		setNoCoverData(out.toByteArray());
		super.init(config);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doProcess(req, resp);
		}
		catch (IOException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		Artwork artwork = null;
		String albumPath = req.getPathInfo();
		byte[] data;
		String mimeType;
		if (!StringUtils.isEmpty(albumPath)) {
			albumPath = albumPath.substring(1);
			File flacAlbumDirectory = new File(getFlacDirectory(), albumPath);
			if (flacAlbumDirectory.exists() && flacAlbumDirectory.isDirectory()) {
				File[] flacFiles = flacAlbumDirectory.listFiles(this);
				for (int idx = 0; artwork == null && idx < flacFiles.length; idx++) {
					artwork = extractArtworkFromFlacFile(flacFiles[idx]);
				}
			}
		}
		if (artwork == null) {
			data = getNoCoverData();
			mimeType = "image/png";
		}
		else {
			mimeType = artwork.getMimeType();
			data = artwork.getBinaryData();
		}
		resp.setContentType(mimeType);
		resp.setContentLength(data.length);
		OutputStream out = resp.getOutputStream();
		try {
			IOUtils.copy(new ByteArrayInputStream(data), out);
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}

	protected Artwork extractArtworkFromFlacFile(File flacFile) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		AudioFile audioFile = AudioFileIO.read(flacFile);
		List<Artwork> artworkList = audioFile.getTag().getArtworkList();
		return artworkList == null || artworkList.isEmpty()?null:artworkList.get(0);
	}

	public boolean accept(File pathname) {
		return "flac".equals(FilenameUtils.getExtension(pathname.getName()));
	}
	
	public File getFlacDirectory() {
		return i_flacDirectory;
	}

	public void setFlacDirectory(File flacDirectory) {
		i_flacDirectory = flacDirectory;
	}

	public byte[] getNoCoverData() {
		return i_noCoverData;
	}

	public void setNoCoverData(byte[] noCoverData) {
		i_noCoverData = noCoverData;
	}
}
