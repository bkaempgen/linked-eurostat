package com.ontologycentral.estatwrap.convert;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

/**
 * Create the table_of_contents.rdf and table_of_contents.html files that are used in the webapp.
 * 
 * @author aharth
 */
public class CreateToCTest extends TestCase {
	Logger _log = Logger.getLogger(this.getClass().getName());

	/**
	 */
	public void testCreateToC() throws Exception {
		for (String format : new String[] { "rdf", "html" } ) {
			URL u = new URL("http://ec.europa.eu/eurostat/estat-navtree-portlet-prod/BulkDownloadListing?sort=1&file=table_of_contents.xml");

			HttpURLConnection conn = (HttpURLConnection)u.openConnection();
			InputStream is = conn.getInputStream();

			TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", Thread.currentThread().getContextClassLoader()); 

			Transformer t = tf.newTransformer(new StreamSource("contrib/toc-" + format + ".xsl"));

			FileOutputStream fos = new FileOutputStream("src/main/webapp/table_of_contents." + format);

			StreamSource ssource = new StreamSource(is);
			StreamResult sresult = new StreamResult(fos);

			_log.log(Level.INFO, "Creating toc file in {0} for webapp", format);
			t.transform(ssource, sresult);

			is.close();
			fos.close();
		}
	}
}
