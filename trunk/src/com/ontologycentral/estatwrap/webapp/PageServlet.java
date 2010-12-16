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

import com.ontologycentral.estatwrap.convert.Data;

@SuppressWarnings("serial")
public class PageServlet extends HttpServlet {
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

		resp.setContentType("text/html");
		
		OutputStream os = resp.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os , "UTF-8");

		String id = req.getRequestURI();
		id = id.substring("/page/".length());

		ServletContext ctx = getServletContext();

		resp.setHeader("Cache-Control", "public");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

		XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);
    		
		Map<String, String> toc = (Map<String, String>)ctx.getAttribute(Listener.TOC);

		try {    		
			XMLStreamWriter ch = factory.createXMLStreamWriter(os, "utf-8");
			ch.writeStartDocument("utf-8", "1.0");

			ch.writeStartElement("html");
			
			ch.writeStartElement("head");
			ch.writeStartElement("title");
			ch.writeCharacters(toc.get(id));
			ch.writeEndElement();

			ch.writeStartElement("body");

			ch.writeStartElement("p");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "../");
			ch.writeCharacters("Eurostat Wrapper Home");
			ch.writeEndElement();
			ch.writeEndElement();

			ch.writeStartElement("h1");
			ch.writeCharacters(toc.get(id));
			ch.writeEndElement();

			ch.writeStartElement("p");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://kiechle.e8u.de/hiwi/getImage.html?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+eus%3A+%3Chttp%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23%3E%0D%0A%0D%0ASELECT+%3Ftime+%3Fvalue+%3Fgeo%0D%0AWHERE+{%0D%0A+%3Fs+rdf%3Atype+%3Chttp%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + "%23class%3E+%3B%0D%0A+++eus%3Atime+%3Ftime+%3B%0D%0A+++eus%3Ageo+%3Fgeo+%3B%0D%0A+++rdf%3Avalue+%3Fvalue+.%0D%0A}%0D%0A%0D%0A%0D%0A%09&graph=graph");
			ch.writeCharacters("Visualisation");
			ch.writeEndElement();
			ch.writeEndElement();
				
			ch.writeStartElement("p");
			ch.writeCharacters("Source: ");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://epp.eurostat.ec.europa.eu/");
			ch.writeCharacters("Eurostat");
			ch.writeEndElement();

			ch.writeCharacters(" via ");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://estatwrap.ontologycentral.com/");
			ch.writeCharacters("estatwrap");
			ch.writeEndElement();

			ch.writeStartElement("p");
			ch.writeCharacters("Data available as: ");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "../data/" + id);
			ch.writeCharacters("RDF");
			ch.writeEndElement();

			ch.writeCharacters(" and ");

			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=data/" + id + ".tsv.gz");
			ch.writeCharacters("TSV (for Excel)");
			ch.writeEndElement();	

			ch.writeEndElement();
			
			ch.writeEndElement();

			ch.writeEndElement();
			
			ch.writeEndDocument();

			ch.close();
		} catch (XMLStreamException e) {
			resp.sendError(500, e.getMessage());
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			resp.sendError(500, e.getMessage());
			e.printStackTrace();
			return;			
		}

		os.close();
	}
}
