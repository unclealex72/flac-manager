import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uk.co.unclealex.music.albumcover.service.SignedRequestsServiceImpl;


public class Rest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SignedRequestsServiceImpl signedRequestsService = new SignedRequestsServiceImpl();
		signedRequestsService.setEndpoint("ecs.amazonaws.co.uk");
		signedRequestsService.setAwsAccessKeyId("1GK0A6KVNQEFVH5Q1Z82");
		signedRequestsService.setAwsSecretKey("jq/BOXh4dOYXsso3jn/N8FGF7/40YCPHmTyas1ZF");
		signedRequestsService.initialise();
		String signedUrl = signedRequestsService.sign(createParameters());
		System.out.println(signedUrl);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new URL(signedUrl));
		new XMLOutputter(Format.getPrettyFormat()).output(doc, System.out);
	}
	
	public static Map<String, String> createParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("Service", "AWSECommerceService");
		params.put("Operation", "ItemSearch");
		params.put("SearchIndex", "Music");
		params.put("Artist", "Metallica");
		params.put("Title", "Death Magnetic");
		params.put("ResponseGroup", "Images");
		return params;
	}
}
