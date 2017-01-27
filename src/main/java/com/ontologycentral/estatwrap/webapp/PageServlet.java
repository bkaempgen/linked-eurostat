package com.ontologycentral.estatwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ontologycentral.estatwrap.Main;

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

		// 2 h
		resp.setHeader("Cache-Control", "public,max-age=7200");

//		resp.setHeader("Cache-Control", "public");
//		Calendar c = Calendar.getInstance();
//		//c.add(Calendar.DATE, 1);
//		c.add(Calendar.HOUR, 2);
//		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

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
			ch.writeAttribute("src", "../vis/date.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();

			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js");
			//ch.writeAttribute("src", "http://ajax.microsoft.com/ajax/jquery/jquery-1.5.min.js");
			//http://ajax.microsoft.com/ajax/jquery/jquery-1.4.2.min.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();

			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "../vis/vis.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();
			
			ch.writeStartElement("script");
			ch.writeAttribute("type", "text/javascript");
			ch.writeAttribute("src", "../vis/dsd.js");
			ch.writeCharacters(" ");
			ch.writeEndElement();
			
			ch.writeEndElement();

			String query = "PREFIX sdmx-measure: <http://purl.org/linked-data/sdmx/2009/measure#>\nPREFIX dcterms: <http://purl.org/dc/terms/>\nPREFIX eus: <http://ontologycentral.com/2009/01/eurostat/ns#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX qb: <http://purl.org/linked-data/cube#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n\nSELECT ?time ?value ?geo\nFROM <http://estatwrap.ontologycentral.com/data/" + id + ">\nFROM <http://estatwrap.ontologycentral.com/dic/geo>\nWHERE {\n\t?s qb:dataSet <http://estatwrap.ontologycentral.com/id/" + id + "#ds> .\n\t?s dcterms:date ?time .\n\t?s eus:geo ?g .\n\t?g rdfs:label ?geo .\n\t?s sdmx-measure:obsValue ?value .\n\tFILTER (lang(?geo) = \"en\")\n}\nORDER BY ?geo";
			//String query = "PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.+FILTER+(lang(%3Fgeo)+%3D+\"en\")+}+ORDER+BY+%3Fgeo";
			
			ch.writeStartElement("body");

			ch.writeStartElement("p");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "../");
			ch.writeCharacters("Eurostat Wrapper Home");
			ch.writeEndElement();
			ch.writeEndElement();

			ch.writeStartElement("h1");
			String link = "../id/" + id;
			ch.writeStartElement("a");
			ch.writeAttribute("href", link);
			String title = toc.get(id);
			if (title == null || title.length() == 0) {
				title = "title here";
			}
			ch.writeCharacters(title);
			ch.writeEndElement();
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

			/*
			ch.writeStartElement("h3");
			ch.writeCharacters("Map");
			ch.writeEndElement();
						
			ch.writeStartElement("div");
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://demos.inf.ed.ac.uk:8836/hackday/eurostatHackday.html?query=" + URLEncoder.encode(query, "utf-8"));

			ch.writeCharacters("Map visualisation (via Xi Bai)");
			ch.writeEndElement();
			ch.writeEndElement();
			*/

			ch.writeStartElement("h2");
			ch.writeCharacters("SPARQL Queries");
			ch.writeEndElement();

			ch.writeStartElement("h3");
			ch.writeCharacters("Observations");
			ch.writeEndElement();

			ch.writeStartElement("div");
			
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://ontologycentral.com/sparql?query=" + URLEncoder.encode(query, "utf-8"));
			//"PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.+FILTER+(lang(%3Fgeo)+%3D+\"en\")+}");
			ch.writeCharacters("SPARQL results");
			ch.writeEndElement();

			ch.writeStartElement("pre");
			ch.writeStartElement("code");
//			ch.writeStartElement("form");
//			ch.writeAttribute("method", "get");
//			ch.writeAttribute("action", "http://ontologycentral.com/sparql");
//			ch.writeAttribute("id", "queryform");
//			ch.writeStartElement("textarea");
//			ch.writeAttribute("name", "query");
			ch.writeAttribute("id", "query");
			ch.writeAttribute("rows", "20");
			ch.writeAttribute("cols", "80");
			//ch.writeCharacters(URLDecoder.decode("PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.%0D%0A}%0D%0A", "utf-8"));
			ch.writeCharacters(query);
			//"PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.%0D%0A}%0D%0A", "utf-8"));
			ch.writeEndElement();
//			ch.writeStartElement("input");
//			ch.writeAttribute("type", "submit");
//			ch.writeEndElement();
			ch.writeEndElement();
			
			// div
			ch.writeEndElement();

			
			ch.writeStartElement("h3");
			ch.writeCharacters("Dimensions/Measures");
			ch.writeEndElement();

			ch.writeStartElement("div");
			
			String dsdquery = "PREFIX  qb: <http://purl.org/linked-data/cube#>\n" +
					"SELECT *\n" +
					"FROM <http://estatwrap.ontologycentral.com/dsd/"  + id + ">\n" +
					"WHERE {\n" +
					"\t{\n" +
					"\t\t<http://estatwrap.ontologycentral.com/dsd/" + id + "#dsd> qb:component ?c .\n" +
					"\t\t?c qb:measure ?m .\n" +
					"\t} UNION {\n" +
					"\t\t<http://estatwrap.ontologycentral.com/dsd/" + id + "#dsd> qb:component ?c .\n" +
					"\t\t?c qb:dimension ?m .\n" +
					"\t}\n" +
					"}";

			
			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://ontologycentral.com/sparql?query=" + URLEncoder.encode(dsdquery, "utf-8"));
			//"PREFIX++sdmx-measure%3A+<http%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23>%0D%0APREFIX++eus%3A++<http%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23>%0D%0APREFIX++rdf%3A++<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX++qb%3A+++<http%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23>%0D%0APREFIX++rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0A%0D%0ASELECT++%3Ftime+%3Fvalue+%3Fgeo%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2F" + id + ">%0D%0AFROM+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fgeo>%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+<http%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2F" + id + "%23ds>+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fg+.%0D%0A++++%3Fg+rdfs%3Alabel+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.+FILTER+(lang(%3Fgeo)+%3D+\"en\")+}");
			ch.writeCharacters("SPARQL results");
			ch.writeEndElement();

			ch.writeStartElement("pre");
			ch.writeStartElement("code");
			ch.writeAttribute("id", "dsdquery");
						
			ch.writeCharacters(dsdquery);
			ch.writeEndElement();
			ch.writeEndElement();

			// div
			ch.writeEndElement();
			
			ch.writeStartElement("div");
			ch.writeAttribute("id", "dsdprogress");
			ch.writeStartElement("img");
			ch.writeAttribute("src", "../vis/wait30trans.gif");			
			ch.writeEndElement();
			ch.writeEndElement();

			ch.writeStartElement("div");
			ch.writeAttribute("id", "dsderror");
			ch.writeEndElement();
			
			ch.writeStartElement("ul");
			ch.writeAttribute("id", "dsd");
			ch.writeCharacters(" ");
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
			ch.writeAttribute("href", Main.URI_PREFIX + "?file=data/" + id + ".tsv.gz");
			ch.writeCharacters("TSV (for Excel)");
			ch.writeEndElement();	

			ch.writeCharacters(" and ");

			ch.writeStartElement("a");
			ch.writeAttribute("href", Main.URI_PREFIX + "?file=data/" + id + ".sdmx.zip");
			ch.writeCharacters("SDMX");
			ch.writeEndElement();	

			ch.writeCharacters(" and ");

			ch.writeStartElement("a");
			ch.writeAttribute("href", "http://appsso.eurostat.ec.europa.eu/nui/show.do?dataset=" + id + "&lang=en");
			ch.writeCharacters("Table");
			ch.writeEndElement();	

//			ch.writeCharacters(" and ");

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
