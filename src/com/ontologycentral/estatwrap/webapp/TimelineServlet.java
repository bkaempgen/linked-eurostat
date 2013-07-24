package com.ontologycentral.estatwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@SuppressWarnings("serial")
public class TimelineServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

		String query = req.getParameter("query");
		ServletContext ctx = getServletContext();

		resp.setHeader("Cache-Control", "public");
		Calendar c = Calendar.getInstance();
		//c.add(Calendar.DATE, 1);
		c.add(Calendar.HOUR, 2);
		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

		XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);

		try {    		
			XMLStreamWriter ch = factory.createXMLStreamWriter(os, "utf-8");
			ch.writeStartDocument("utf-8", "1.0");

			ch.writeStartElement("html");
			
			ch.writeStartElement("head");
			ch.writeStartElement("title");
			ch.writeCharacters("Timeline Visualisation");
			ch.writeEndElement();
			
			ch.writeStartElement("link");
			ch.writeAttribute("rel", "stylesheet");
			ch.writeAttribute("href", "style.css");
			ch.writeAttribute("type", "text/css");
			ch.writeEndElement();
			
			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "protovis-r3.2.js");
			ch.writeEndElement();
			
			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js");
		//"http://ajax.microsoft.com/ajax/jquery/jquery-1.5.min.js");
			//ch.writeAttribute("src", "http://ajax.microsoft.com/ajax/jquery/jquery-1.4.2.min.js");
			ch.writeEndElement();
			
			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "../vis/date.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();

			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "vis.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();
			
			ch.writeEndElement();

			ch.writeStartElement("body");

			ch.writeStartElement("p");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "../");
			ch.writeCharacters("Eurostat Wrapper Home");
			ch.writeEndElement();
			ch.writeEndElement();

			ch.writeStartElement("h1");
			ch.writeCharacters("Timeline");
			ch.writeEndElement();

			ch.writeStartElement("h2");
			ch.writeCharacters("Visualisation");
			ch.writeEndElement();
			
			ch.writeStartElement("div");
			ch.writeAttribute("id", "progress");
			ch.writeStartElement("img");
			ch.writeAttribute("src", "../vis/wait30trans.gif");			
			ch.writeEndElement();
			ch.writeEndElement();

			ch.writeStartElement("div");
			ch.writeAttribute("id", "error");
			ch.writeEndElement();

			ch.writeStartElement("div");
			ch.writeAttribute("id", "map");
			ch.writeEndElement();
			
			ch.writeStartElement("h2");
			ch.writeCharacters("Query");
			ch.writeEndElement();

			ch.writeStartElement("div");
			ch.writeStartElement("code");
			ch.writeAttribute("id", "query");
			ch.writeCharacters(URLDecoder.decode(query, "utf-8"));
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
