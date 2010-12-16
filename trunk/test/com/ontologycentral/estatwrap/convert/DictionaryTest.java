package com.ontologycentral.estatwrap.convert;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

public class DictionaryTest extends TestCase {
	public void testData() throws Exception {
		String id = "geo";
		
		URL url = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=dic/en/" + id + ".dic");
		//URL url = new URL("http://europa.eu/estatref/download/everybody/data/" + id + ".tsv.gz");
        
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        InputStream is = conn.getInputStream();
        
//		resp.setHeader("Cache-Control", "public");
//		Calendar c = Calendar.getInstance();
//		c.add(Calendar.DATE, 1);
//		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

        Dictionary d = new Dictionary(new InputStreamReader(is, "ISO-8859-1"));
        
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        XMLStreamWriter ch = factory.createXMLStreamWriter(new FileWriter("test/dic-output.rdf")); //ystem.out);
		ch.writeStartDocument("utf-8", "1.0");

		ch.writeStartElement("rdf:RDF");
		ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		ch.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		ch.writeDefaultNamespace(Data.PREFIX);

		ch.writeStartElement("rdf:Description");
		ch.writeAttribute("rdf:about", "");
		ch.writeStartElement("rdfs:comment");
		ch.writeCharacters("Source: Eurostat (http://epp.eurostat.ec.europa.eu/) via estatwrap (http://estatwrap.ontologycentral.com/).");
		ch.writeEndElement();
		ch.writeStartElement("rdfs:seeAlso");
		ch.writeAttribute("rdf:resource", "http://epp.eurostat.ec.europa.eu/portal/page/portal/about_eurostat/corporate/copyright_licence_policy");
		ch.writeEndElement();
		ch.writeEndElement();
		
        d.convert(ch);
        
        ch.writeEndElement();
        ch.writeEndDocument();
        
        ch.close();

	}
}
