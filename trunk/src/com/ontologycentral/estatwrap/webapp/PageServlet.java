package com.ontologycentral.estatwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
		//c.add(Calendar.DATE, 1);
		c.add(Calendar.HOUR, 2);
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

			ch.writeStartElement("link");
			ch.writeAttribute("rel", "stylesheet");
			ch.writeAttribute("href", "../vis/style.css");
			ch.writeAttribute("type", "text/css");
			ch.writeEndElement();

			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "../vis/protovis-r3.2.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();

			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "http://ajax.microsoft.com/ajax/jquery/jquery-1.4.2.min.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();

			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "../vis/vis.js");
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
			ch.writeCharacters(toc.get(id));
			ch.writeEndElement();

			ch.writeStartElement("h2");
			ch.writeCharacters("Visualisations");
			ch.writeEndElement();
			
			ch.writeStartElement("h3");
			ch.writeCharacters("Timeline");
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
			
			ch.writeStartElement("h3");
			ch.writeCharacters("Map");
			ch.writeEndElement();
			
			String query = "PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.+FILTER+(lang(%3Fgeo)+%3D+\"en\")+}";
			
			ch.writeStartElement("div");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://demos.inf.ed.ac.uk:8836/hackday/eurostatHackday.html?query=" + query);

			ch.writeCharacters("Map visualisation (via Xi Bai)");
			ch.writeEndElement();
			ch.writeEndElement();
			
			ch.writeStartElement("h2");
			ch.writeCharacters("Query");
			ch.writeEndElement();

			ch.writeStartElement("div");
			ch.writeStartElement("code");
			ch.writeAttribute("id", "query");
			//ch.writeCharacters(URLDecoder.decode("PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.%0D%0A}%0D%0A", "utf-8"));
			ch.writeCharacters(URLDecoder.decode(query, "utf-8"));
			//"PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.%0D%0A}%0D%0A", "utf-8"));
			ch.writeEndElement();
			ch.writeEndElement();

			/*
			ch.writeStartElement("p");
			ch.writeStartElement("a");			
			
			ch.writeAttribute("href", "../vis/timeline.html?query=PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.%0D%0A}%0D%0A");
			//ch.writeAttribute("href", "http://kiechle.e8u.de/hiwi/getImage.html?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+eus%3A+%3Chttp%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23%3E%0D%0A%0D%0ASELECT+%3Ftime+%3Fvalue+%3Fgeo%0D%0AWHERE+{%0D%0A+%3Fs+rdf%3Atype+%3Chttp%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + "%23class%3E+%3B%0D%0A+++eus%3Atime+%3Ftime+%3B%0D%0A+++eus%3Ageo+%3Fgeo+%3B%0D%0A+++rdf%3Avalue+%3Fvalue+.%0D%0A}%0D%0A%0D%0A%0D%0A%09&graph=graph");
			ch.writeCharacters("Visualisation");
			ch.writeEndElement();
			ch.writeEndElement();
*/
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
			ch.writeCharacters("Available as: ");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "../data/" + id);
			ch.writeCharacters("RDF (data)");
			ch.writeEndElement();

			ch.writeCharacters("/");

			ch.writeStartElement("a");
			ch.writeAttribute("href", "../dsd/" + id);
			ch.writeCharacters("RDF (data structure definitions)");
			ch.writeEndElement();

			ch.writeCharacters(" and ");

			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=data/" + id + ".tsv.gz");
			ch.writeCharacters("TSV (for Excel)");
			ch.writeEndElement();	

			ch.writeCharacters(" and ");

			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://qcrumb.com/sparql?query=" + query);
			//"PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.+FILTER+(lang(%3Fgeo)+%3D+\"en\")+}");
			ch.writeCharacters("SPARQL results");
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
