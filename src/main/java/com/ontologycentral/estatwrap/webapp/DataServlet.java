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

import com.ontologycentral.estatwrap.Main;
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

		URL url = new URL(Main.URI_PREFIX + "?file=data/" + id + ".tsv.gz");

		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			// here, use a threshold to limit the amount of data converted (GAE limitations)
			//conn.setConnectTimeout(8*1000);
			//conn.setReadTimeout(8*1000);
			// Try maximum of 60 sec for HTTP request? Maybe 55sec
			conn.setConnectTimeout(55*1000);
			conn.setReadTimeout(55*1000);
			// Java ssems to use Cache-Control: no-cache and Pragma: no-cache - why?
			// disable
			conn.setUseCaches(true);

			conn.setRequestProperty("User-Agent", "estatwrap.ontologycentral.com");

			InputStream is = new GZIPInputStream(conn.getInputStream());

			if (conn.getResponseCode() != 200) {
				resp.sendError(conn.getResponseCode());
			}

			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = Listener.DEFAULT_ENCODING;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));

			// 1 hour
    		resp.setHeader("Cache-Control", "max-age=600");

//			resp.setHeader("Cache-Control", "public");
//			Calendar c = Calendar.getInstance();
//			c.add(Calendar.HOUR, 1);
//			resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));
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
