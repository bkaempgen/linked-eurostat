package com.ontologycentral.estatwrap.webapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings("serial")
public class DsdServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		OutputStream os = resp.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os , "UTF-8");

		String id = req.getRequestURI();
		id = id.substring("/dsd/".length());

		ServletContext ctx = getServletContext();

		String archive = "http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=data/" + id + ".sdmx.zip";

		URL u = new URL(archive);

		_log.info("retrieving " + u);
		//System.out.println("retrieving " + _u);

		try {
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("lookup on " + u + " resulted HTTP in status code " + conn.getResponseCode());
			}

			InputStream is = conn.getInputStream();

			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = "ISO-8859-1";
			}

			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));

			ZipEntry ze;
			while((ze = zis.getNextEntry()) != null) {
				if (ze.getName().contains("dsd.xml") ) {
					break;
				}
			}
			/*
		StringWriter writer = new StringWriter();
		try {
			char[] buffer = new char[1024];
			Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();			
		}

		String content = writer.getBuffer().toString();

		_log.info(content);
			 */
			Transformer t = (Transformer)ctx.getAttribute(Listener.SDMX_T);

			resp.setContentType("application/rdf+xml");

			StreamSource ssource = new StreamSource(zis);
			StreamResult sresult = new StreamResult(os);

			_log.info("lapplying xslt");

			t.transform(ssource, sresult);

			zis.close();
		} catch (TransformerException e) {
			e.printStackTrace(); 
			resp.sendError(500, e.getMessage());
			return;
		} catch (IOException e) {
			resp.sendError(500, u + ": " + e.getMessage());
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			resp.sendError(500, u + ": " + e.getMessage());
			e.printStackTrace();
			return;			
		}

		os.close();
	}
}
