package com.ontologycentral.estatwrap.convert;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DictionaryNace extends Dictionary {
	public static String PREFIX = "/dic/";

	Logger _log = Logger.getLogger(this.getClass().getName());

	Map<String, String> _map;

	public DictionaryNace(Reader is, String file) throws IOException {
		super(is, file);
	}

	public void addMappings(XMLStreamWriter out, String id) throws IOException, XMLStreamException {
		super.addMappings(out, id);

		out.writeStartElement("owl:sameAs");
		out.writeAttribute("rdf:resource", "http://rdfdata.eionet.europa.eu/eurostatdic/nace_r2/" + id);				
		out.writeEndElement();

		if (id.length() == 1) {
			out.writeStartElement("owl:sameAs");
			out.writeAttribute("rdf:resource", "http://ec.europa.eu/eurostat/ramon/rdfdata/nace_r2/" + id);				
			out.writeEndElement();
		} else if (id.length() == 3) {
			// check if id.substring(1) is a number
			try {
				Integer.parseInt(id.substring(1));
				out.writeStartElement("owl:sameAs");
				out.writeAttribute("rdf:resource", "http://ec.europa.eu/eurostat/ramon/rdfdata/nace_r2/" + id.substring(1));				
				out.writeEndElement();
			} catch (NumberFormatException e) {
				;
			}
		}
	}
}