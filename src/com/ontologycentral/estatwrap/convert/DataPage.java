package com.ontologycentral.estatwrap.convert;

import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ontologycentral.estatwrap.webapp.Listener;

public class DataPage {
	public static void convert(XMLStreamWriter ch, Map<String, String> toc, String id, Reader in) throws XMLStreamException, IOException {
		ch.writeStartDocument("utf-8", "1.0");

		ch.writeStartElement("rdf:RDF");
		ch.writeDefaultNamespace(Data.PREFIX);
		ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		ch.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		ch.writeNamespace("foaf", "http://xmls.com/foaf/0.1/");
		ch.writeNamespace("qb", "http://purl.org/linked-data/cube#");
		ch.writeNamespace("sdmx-measure", "http://purl.org/linked-data/sdmx/2009/measure#obsValue");
		ch.writeNamespace("dcterms", "http://purl.org/dc/terms/");
		
		ch.writeStartElement("rdf:Description");
		ch.writeAttribute("rdf:about", "");
		ch.writeStartElement("foaf:maker");
		ch.writeAttribute("rdf:resource", "http://harth.org/andreas/foaf#ah");
		ch.writeEndElement();
		Calendar cal = Calendar.getInstance();
		ch.writeStartElement("dcterms:date");
		ch.writeCharacters(Listener.ISO8601.format(cal.getTime()));
		ch.writeEndElement();
		ch.writeEndElement();

		ch.writeStartElement("qb:DataSet");
		//ch.writeAttribute("rdf:about", "/id/" + id  + "#ds");    		
		ch.writeAttribute("rdf:about", "/id/" + id  + "#ds"); 
		if (toc.containsKey(id)) {
			ch.writeStartElement("rdfs:label");
			ch.writeCharacters(toc.get(id));
			ch.writeEndElement();
		}
		ch.writeStartElement("rdfs:comment");
		ch.writeCharacters("Source: Eurostat (http://epp.eurostat.ec.europa.eu/) via estatwrap (http://estatwrap.ontologycentral.com/).");
		ch.writeEndElement();
		ch.writeStartElement("rdfs:seeAlso");
		ch.writeAttribute("rdf:resource", "http://epp.eurostat.ec.europa.eu/portal/page/portal/about_eurostat/corporate/copyright_licence_policy");
		ch.writeEndElement();
		ch.writeStartElement("rdfs:seeAlso");
		ch.writeAttribute("rdf:resource", "http://ontologycentral.com/2009/01/eurostat/");
		ch.writeEndElement();
		ch.writeEndElement();
		
		Data d = new Data(in);
		
        d.convert(ch, id);
        
        ch.writeEndElement();
        ch.writeEndDocument();
	}
}
