package com.ontologycentral.estatwrap.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ontologycentral.estatwrap.convert.Data;
import com.ontologycentral.estatwrap.convert.Dictionary;
import com.ontologycentral.estatwrap.convert.DictionaryGeo;
import com.ontologycentral.estatwrap.convert.DictionaryNace;
import com.ontologycentral.estatwrap.convert.DictionaryPage;
import com.ontologycentral.estatwrap.convert.DictionaryUnits;

@SuppressWarnings("serial")
public class DictionaryServlet extends HttpServlet {
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
		OutputStreamWriter osw = new OutputStreamWriter(os , "UTF-8");

		String id = req.getRequestURI();
		id = id.substring("/dic/".length());

		ServletContext ctx = getServletContext();
		Cache cache = (Cache)ctx.getAttribute(Listener.CACHE);
		StringReader sr = null;
		
		try {
   			URL url = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=dic/en/" + id + ".dic");
   			//URL url = new URL("http://europa.eu/estatref/download/everybody/dic/en/" + id + ".dic");
            
            if (cache.containsKey(url)) {
            	sr = new StringReader((String)cache.get(url));
            }

            if (sr == null) {
            	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            	InputStream is = conn.getInputStream();
            
            	if (conn.getResponseCode() != 200) {
            		resp.sendError(conn.getResponseCode());
            		return;
            	}
            	
            	String encoding = conn.getContentEncoding();
        		if (encoding == null) {
        			encoding = "ISO-8859-1";
        		}
            	
            	BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));
            	String l;
            	StringBuilder sb = new StringBuilder();
            	
            	while ((l = in.readLine()) != null) {
            		sb.append(l);
            		sb.append('\n');
            	}
            	in.close();
            	
            	String str = sb.toString();
            	sr = new StringReader(str);
            	
            	try {
            		cache.put(url, str);
            	} catch (RuntimeException e) {
   					_log.info(e.getMessage());
            	}
            }
            
            resp.setHeader("Cache-Control", "public");
    		Calendar c = Calendar.getInstance();
    		c.add(Calendar.DATE, 1);
    		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

    		XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);

            XMLStreamWriter ch = factory.createXMLStreamWriter(osw);

            DictionaryPage.convert(ch, id, sr);
            
            ch.close();
		} catch (IOException e) {
			resp.sendError(500, e.getMessage());
			e.printStackTrace();
			return;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			resp.sendError(500, e.getMessage());
			return;
		}  
	}
}
