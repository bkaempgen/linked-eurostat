package com.ontologycentral.estatwrap.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ontologycentral.estatwrap.convert.DataPage;

@SuppressWarnings("serial")
public class DataServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (req.getServerName().contains("estatwrap.appspot.com")) {
			try {
				URI re = new URI("http://estatwrap.ontologycentral.com/" + req.getRequestURI());
				re = re.normalize();
				resp.sendRedirect(re.toString());
			} catch (URISyntaxException e) {
				resp.sendError(500, e.getMessage());
			}
			return;
		}

		resp.setContentType("application/rdf+xml");

		OutputStream os = resp.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os , "UTF-8");

		String id = req.getRequestURI();
		id = id.substring("/data/".length());

		ServletContext ctx = getServletContext();

		URL url = new URL(Listener.URI_PREFIX + "?file=data/" + id + ".tsv.gz");

		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(8*1000);
			conn.setReadTimeout(8*1000);
			
			InputStream is = new GZIPInputStream(conn.getInputStream());

			if (conn.getResponseCode() != 200) {
				resp.sendError(conn.getResponseCode());
			}

			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = "ISO-8859-1";
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));

			resp.setHeader("Cache-Control", "public");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, 1);
			resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));
			//resp.setHeader("Content-Encoding", "gzip");

			XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);

			Map<String, String> toc = (Map<String, String>)ctx.getAttribute(Listener.TOC);

			XMLStreamWriter ch = factory.createXMLStreamWriter(os, "utf-8");

			DataPage.convert(ch, toc, id, in);

			ch.close();
		} catch (IOException e) {
			resp.sendError(500, url + ": " + e.getMessage());
			e.printStackTrace();
			return;
		} catch (XMLStreamException e) {
			resp.sendError(500, url + ": " + e.getMessage());
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			resp.sendError(500, url + ": " + e.getMessage());
			e.printStackTrace();
			return;			
		}

		os.close();
	}
}
