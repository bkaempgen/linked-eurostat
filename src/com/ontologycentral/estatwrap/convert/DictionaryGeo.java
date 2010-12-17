package com.ontologycentral.estatwrap.convert;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DictionaryGeo extends Dictionary {
	public static String PREFIX = "/dic/";
	
	Logger _log = Logger.getLogger(this.getClass().getName());

	String[] COUNTRIES = new String[] { "AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK", "HR", "IS", "MK", "TR", "LI", "NO", "CH" };
	Set<String> _c;
	
	String[] EUC = new String[] {
			"AD",
			"AL",
			"AM",
			"AT",
			"AZ",
			"BA",
			"BE",
			"BG",
			"BY",
			"CH",
			"CY",
			"CZ",
			"DE",
			"DK",
			"EE",
			"EFTA4",
			"EIONET",
			"ES",
			"EU15",
			"EU25",
			"EU27",
			"EU6",
			"FI",
			"FR",
			"GB",
			"GE",
			"GR",
			"HR",
			"HU",
			"IE",
			"IS",
			"IT",
			"KZ",
			"LI",
			"LT",
			"LU",
			"LV",
			"MC",
			"MD",
			"ME",
			"MK",
			"MT",
			"NL",
			"NO",
			"PL",
			"PT",
			"RO",
			"RS",
			"RU",
			"SE",
			"SI",
			"SK",
			"SM",
			"TR",
			"UA",
			"XK"
	};
	
	Set<String> _ec;

	Map<String, String> _map;
	
	public DictionaryGeo(Reader is) throws IOException {
		super(is);
		_c = new HashSet<String>(Arrays.asList(COUNTRIES));
		_ec = new HashSet<String>(Arrays.asList(EUC));
		_map = new HashMap<String, String>();
		
		_map.put("AR", "Argentina");
		_map.put("AT", "Austria");
		_map.put("BE", "Belgium");
		_map.put("BO", "Bolivia");
		_map.put("BA", "Bosnia_and_Herzegovina");
		_map.put("BR", "Brazil");
		_map.put("BG", "Bulgaria");
		_map.put("CN", "China");
		_map.put("CO", "Colombia");
		_map.put("HR", "Croatia");
		_map.put("CZ", "Czech_Republic_the");
		_map.put("DK", "Denmark");
		_map.put("EE", "Estonia");
		_map.put("FI", "Finland");
		_map.put("FR", "France");
		_map.put("DE", "Germany");
		_map.put("GR", "Greece");
		_map.put("HU", "Hungary");
		_map.put("IT", "Italy");
		_map.put("JP", "Japan");
		_map.put("LV", "Latvia");
		_map.put("LI", "Liechtenstein");
		_map.put("LT", "Lithuania");
		_map.put("LU", "Luxembourg");
		_map.put("MT", "Malta");
		_map.put("MX", "Mexico");
		_map.put("NL", "Netherlands_the");
		_map.put("PE", "Peru");
		_map.put("PL", "Poland");
		_map.put("PT", "Portugal");
		_map.put("RO", "Romania");
		_map.put("SK", "Slovakia");
		_map.put("SI", "Slovenia");
		_map.put("ES", "Spain");
		_map.put("SE", "Sweden");
		_map.put("CH", "Switzerland");
//		_map.put("UK", "United_Kingdom_of_Great_Britain_and_Northern_Ireland__the");
		_map.put("US", "United_States_of_America");
		_map.put("UY", "Uruguay");
	}

	public void addMappings(XMLStreamWriter out, String id) throws IOException, XMLStreamException {
		if (_c.contains(id)) {
			out.writeStartElement("owl:sameAs");
			out.writeAttribute("rdf:resource", "http://ec.europa.eu/eurostat/ramon/rdfdata/countries.rdf#" + id);				
			out.writeEndElement();
		}
		
		if (_ec.contains(id)) {
			out.writeStartElement("owl:sameAs");
			out.writeAttribute("rdf:resource", "http://rdfdata.eionet.europa.eu/eea/countries/" + id);				
			out.writeEndElement();
		}
		
		if (_map.containsKey(id)) {
			out.writeStartElement("owl:sameAs");
			out.writeAttribute("rdf:resource", "http://aims.fao.org/geopolitical.owl#" + _map.get(id));				
			out.writeEndElement();			
		}

		out.writeStartElement("owl:sameAs");
		out.writeAttribute("rdf:resource", "http://eris.okfn.org/ww/2010/12/eurostat/nuts#" + id);				
		out.writeEndElement();
	}
}