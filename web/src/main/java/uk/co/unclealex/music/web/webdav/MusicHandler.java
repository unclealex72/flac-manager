package uk.co.unclealex.music.web.webdav;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.DavResource;

import uk.co.unclealex.music.core.service.filesystem.ManagedRepository;
import uk.co.unclealex.music.core.service.filesystem.RepositoryManager;

public class MusicHandler extends DefaultHandler implements IOHandler {

	@Override
	/**
	 * Return an altered content node that has an extra jcr:data property containing
	 * the required inputStream and length.
	 */
	protected Node getContentNode(ExportContext context, boolean isCollection)
			throws RepositoryException {
		Node contentNode = super.getContentNode(context, isCollection);
		if (!isCollection) {
			contentNode = decorateNode(contentNode);
		}
		return contentNode;
	}
	
	protected Node decorateNode(Node contentNode) throws RepositoryException {
		Repository repository = contentNode.getSession().getRepository();
		if (!(repository instanceof ManagedRepository)) {
			throw new RepositoryException("A managed repository is required by this handler.");
		}
		RepositoryManager repositoryManager = ((ManagedRepository) repository).getRepositoryManager();
		Property dataProperty = new DataProperty(contentNode, repositoryManager);
		return
			new ExtraPropertiesNodeDecorator( 
				new RemovingPropertiesNodeDecorator(
						contentNode, RepositoryManager.PROPERTY_ID, RepositoryManager.PROPERTY_LENGTH),
				dataProperty);
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

}
