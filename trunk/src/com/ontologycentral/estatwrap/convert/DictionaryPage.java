package com.ontologycentral.estatwrap.convert;

import java.io.IOException;
import java.io.Reader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DictionaryPage {
	public static void convert(XMLStreamWriter ch, String id, Reader sr) throws XMLStreamException, IOException {
		ch.writeStartDocument("utf-8", "1.0");

		ch.writeStartElement("rdf:RDF");
		ch.writeDefaultNamespace(Data.PREFIX);
		ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		ch.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		ch.writeNamespace("owl", "http://www.w3.org/2002/07/owl#");
		ch.writeNamespace("foaf", "http://xmls.com/foaf/0.1/");
		ch.writeNamespace("skos", "http://www.w3.org/2004/02/skos/core#");
		
		ch.writeStartElement("rdf:Description");
		ch.writeAttribute("rdf:about", "");
		ch.writeStartElement("rdfs:comment");
		ch.writeCharacters("Source: Eurostat (http://epp.eurostat.ec.europa.eu/) via Linked Eurostat (http://estatwrap.ontologycentral.com/).");
		ch.writeEndElement();
		ch.writeStartElement("foaf:maker");
		ch.writeAttribute("rdf:resource", "http://harth.org/andreas/foaf#ah");
		ch.writeEndElement();
		ch.writeStartElement("rdfs:seeAlso");
		ch.writeAttribute("rdf:resource", "http://epp.eurostat.ec.europa.eu/portal/page/portal/about_eurostat/corporate/copyright_licence_policy");
		ch.writeEndElement();
		ch.writeStartElement("rdfs:seeAlso");
		ch.writeAttribute("rdf:resource", "http://ontologycentral.com/2009/01/eurostat/");
		ch.writeEndElement();
		ch.writeEndElement();

        Dictionary d = null;
        
        if ("geo".equals(id)) {
        	d = new DictionaryGeo(sr);
        } else if ("unit".equals(id)) {
        	d = new DictionaryUnits(sr);
        } else if ("nace".equals(id)) {
        	d = new DictionaryNace(sr);
        } else {
        	d = new Dictionary(sr);            	
        }
        
        d.convert(ch);
        
        ch.writeEndElement();
        ch.writeEndDocument();
	}
}
