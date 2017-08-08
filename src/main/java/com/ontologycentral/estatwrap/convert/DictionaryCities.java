package com.ontologycentral.estatwrap.convert;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DictionaryCities extends Dictionary {
	public static String PREFIX = "/dic/";

	Logger _log = Logger.getLogger(this.getClass().getName());

	public DictionaryCities(Reader is, String file) throws IOException {
		super(is, file);
	}

	public void convert(XMLStreamWriter out, String lang) throws IOException, XMLStreamException {
		String line = null;

		while ((line = _in.readLine()) != null) {
			line = line.trim();
			if (line.length() <= 0) {
				continue;
			}
			try {
				StringTokenizer st = new StringTokenizer(line, "\t");
				String id = st.nextToken().trim();
				String label = st.nextToken().trim();

				out.writeStartElement("rdf:Description");
				out.writeAttribute("rdf:ID", id);

				out.writeStartElement("rdfs:label");
				out.writeAttribute("xml:lang", lang);
				out.writeCharacters(label);
				out.writeEndElement();

				out.writeStartElement("skos:notation");
				out.writeCharacters(id);
				out.writeEndElement();

				out.writeStartElement("ramon:partOf");
				out.writeAttribute("rdf:resource", "http://rdfdata.eionet.europa.eu/eea/countries/" + id.substring(0, 2));				
				out.writeEndElement();

				addMappings(out, id);

				out.writeEndElement();
			} catch (NoSuchElementException ne) {
				System.err.println(line + " " + ne);
			}
		}

		_in.close();
	}
}