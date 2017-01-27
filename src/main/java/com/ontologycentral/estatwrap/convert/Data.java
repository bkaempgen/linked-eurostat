package com.ontologycentral.estatwrap.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Data {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public static String PREFIX = "http://ontologycentral.com/2009/01/eurostat/ns#";
	
	BufferedReader _in;
	
	// here, use a threshold to limit the amount of data converted (GAE limitations)
	// added * 8 to remove restriction
	public static int MAX_COLS = 8*10;
	public static int MAX_ROWS = 1024*8*10;
	
	public Data(Reader sr) throws IOException {
		_in = new BufferedReader(sr);
	}
	
	public void convert(XMLStreamWriter out, String id) throws IOException, XMLStreamException {
		String line = null;

		int rows = 0;
		Header h = null;
		Line l = null;

		if ((line = _in.readLine()) != null) {
			++rows;
			line = line.trim();
			if (line.length() <= 0) {
				throw new IOException("could not read header!");
			}

			h = new Header(line);
		}
		
		while ((line = _in.readLine()) != null) {
			++rows;
			line = line.trim();
			if (line.length() <= 0) {
				continue;
			}

			l = new Line(line);

			printTriple(h, l, out, rows, id);
			
			if (rows > MAX_ROWS) {
				break;
			}
		}

		_in.close();
	}

	public void printTriple(Header h, Line l, XMLStreamWriter out, int bnodeid, String id) throws XMLStreamException {
		List<String> hd1 = h.getDim1();
		List<String> ld1 = l.getDim1();

		if (hd1.size() != ld1.size()) {
			System.err.println("header dimensions and line dimensions don't match!");
		}

		List<String> hcol = h.getCols();
		List<String> lcol = l.getCols();

		if (hcol.size() != lcol.size()) {
			System.err.println("header columns and line columns don't match!");
		}
		
		int start = 0;
		int end = Math.min(hcol.size(), MAX_COLS);
		
		// hack - some stats are sorted from oldest to newest, some the other way round
		// check if the last entry contains year 200x or 201x
		String last = (String)hcol.get(hcol.size()-1);
		//System.out.println(last);
		if (last.contains("200") || last.contains("201")) {
			start = hcol.size()-MAX_COLS;
			if (start < 0) {
				start = 0;
			}
			end = hcol.size();
		}
		
		if (start != 0 || end != hcol.size()) {
    		out.writeStartElement("rdf:Description");
    		out.writeAttribute("rdf:about", "");
    		out.writeStartElement("rdfs:comment");
    		out.writeCharacters("ATTENTION: file may be truncated due to processing time restrictions.");
    		out.writeEndElement();
    		out.writeEndElement();
		}

		for (int i = start; i < end; ++i) {
			if (((String)lcol.get(i)).trim().equals(":")) {
				continue;
			}
			
    		out.writeStartElement("qb:Observation");
    		
    		out.writeStartElement("qb:dataSet");
    		out.writeAttribute("rdf:resource", "/id/" + id + "#ds");
    		// @@@ workaround to get query processor to function
    		//out.writeAttribute("rdf:resource", id + "#ds");

    		out.writeEndElement();

			for (int j = 0; j < hd1.size(); ++j) {
				if (hd1.get(j).equals("time")) {
					String time = convertTime(ld1.get(j));
					out.writeStartElement("dcterms:date");
					out.writeCharacters(time);
					out.writeEndElement();
				} else {
					out.writeStartElement(hd1.get(j));
					out.writeAttribute("rdf:resource", Dictionary.PREFIX + hd1.get(j) + "#" + ld1.get(j));
					out.writeEndElement();
				}
			}
			
			if (h.getDim2().equals("time")) {
				String time = convertTime(hcol.get(i));
	    		out.writeStartElement("dcterms:date");
	    		out.writeCharacters(time);
	    		out.writeEndElement();
			} else {
				out.writeStartElement(h.getDim2());
				out.writeAttribute("rdf:resource", Dictionary.PREFIX + h.getDim2() + "#" + hcol.get(i));
				out.writeEndElement();
			}

			//http://purl.org/linked-data/sdmx/2009/measure#obsValue
			// do not print empty values
			String val = (String)lcol.get(i).trim();
			if (!val.equals(":")) {
				out.writeStartElement("sdmx-measure:obsValue");

				String note = null;

				// Different encodings of values
				if (val.indexOf(' ') > 0) {
					note = val.substring(val.indexOf(' ')+1);
					val = val.substring(0, val.indexOf(' '));
					// Datatype is double, always.
					//out.writeAttribute("rdf:datatype", Dictionary.PREFIX + "note#" + note);
					if (val.equals(":")) {
						val = "";
						out.writeAttribute("rdf:datatype", "http://www.w3.org/2001/XMLSchema#string");
					} else {
						out.writeAttribute("rdf:datatype", "http://www.w3.org/2001/XMLSchema#double");
					}

				} else if (val.equals(":")) {
					val = "";
				} else {
					try {
						double d = Double.parseDouble(val);
					} catch (NumberFormatException e) {
						_log.info("not a number " + val);
						val = "";
					}
					out.writeAttribute("rdf:datatype", "http://www.w3.org/2001/XMLSchema#double");
				}

				out.writeCharacters(val);
				if (note != null) {
		    		out.writeStartElement("rdfs:comment");
		    		out.writeCharacters(note);
		    		out.writeEndElement();	
				}

				out.writeEndElement();
			}
    		out.writeEndElement();
		}
	}
	
	String convertTime(String time) {
		if (time.length() > 4) {
			if (time.contains("M")) {
				time = time.replace('M', '-');
			} else if (time.contains("D")) {
				time = time.replace('D', '-');
			} else if (time.charAt(4) == 'Q') {
				String quarter = time.substring(4);
				//System.out.println(quarter);
				time = time.substring(0, 4);
				if (quarter.equals("Q1")) {
					time += "-03-31";
				} else if (quarter.equals("Q2")) {
					time += "-06-30";
				} else if (quarter.equals("Q3")) {
					time += "-09-30";							
				} else if (quarter.equals("Q4")) {
					time += "-12-31";
				}
			}
		}
		
		return time;
	}
}