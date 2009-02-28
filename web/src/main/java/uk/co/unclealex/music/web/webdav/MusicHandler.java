package uk.co.unclealex.music.web.webdav;

import java.io.IOException;
import java.io.OutputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.DavResource;

import uk.co.unclealex.music.base.io.DataExtractor;
import uk.co.unclealex.music.base.io.InputStreamCopier;
import uk.co.unclealex.music.base.io.KnownLengthOutputStream;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

public class MusicHandler extends DefaultHandler implements IOHandler {

	private DataExtractor<EncodedTrackBean> i_encodedTrackDataExtractor;
	private InputStreamCopier<EncodedTrackBean> i_inputStreamCopier;
	
	public MusicHandler() {
		SpringWebdavServlet.APPLICATION_CONTEXT.getAutowireCapableBeanFactory().autowireBean(this);
	}
	
	@Override
	public boolean canImport(ImportContext context, boolean isCollection) {
		return false;
	}

	@Override
	public boolean canImport(ImportContext context, DavResource resource) {
		return false;
	}

	@Override
	public boolean importContent(ImportContext context, boolean isCollection)
			throws IOException {
		return false;
	}

	@Override
	public boolean importContent(ImportContext context, DavResource resource)
			throws IOException {
		return false;
	}

    /**
     * Checks if the given content node contains a jcr:data property
     * and spools its value to the output stream of the export context.<br>
     * Please note, that subclasses that define a different structure of the
     * content node should create their own
     * {@link  #exportData(ExportContext, boolean, Node) exportData} method.
     *
     * @param context export context
     * @param isCollection <code>true</code> if collection
     * @param contentNode the content node
     * @throws IOException if an I/O error occurs
     */
    protected void exportData(ExportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        if (contentNode.hasProperty(RepositoryManager.PROPERTY_ID)) {
            Property p = contentNode.getProperty(RepositoryManager.PROPERTY_ID);
            int id = (int) p.getLong();
            KnownLengthOutputStream<OutputStream> out = new KnownLengthOutputStream<OutputStream>(context.getOutputStream()) {
            	@Override
            	public void setLength(int length) throws IOException {
            		// Do nothing
            	}
            };
            getInputStreamCopier().copy(getEncodedTrackDataExtractor(), id, out);
        } // else: stream undefined -> contentlength was not set
    }

	public DataExtractor<EncodedTrackBean> getEncodedTrackDataExtractor() {
		return i_encodedTrackDataExtractor;
	}

	public void setEncodedTrackDataExtractor(DataExtractor<EncodedTrackBean> encodedTrackDataExtractor) {
		i_encodedTrackDataExtractor = encodedTrackDataExtractor;
	}

	public InputStreamCopier<EncodedTrackBean> getInputStreamCopier() {
		return i_inputStreamCopier;
	}

	public void setInputStreamCopier(InputStreamCopier<EncodedTrackBean> inputStreamCopier) {
		i_inputStreamCopier = inputStreamCopier;
	}

}
