package com.ontologycentral.estatwrap.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

public class ToC {
	Logger _log = Logger.getLogger(this.getClass().getName());

	BufferedReader _in;

	public ToC(InputStream is, String encoding) throws IOException {
		if (encoding == null) {
			encoding = "ISO_8859-1";
		}

		_in = new BufferedReader(new InputStreamReader(is, encoding));
	}
	
	public Map<String, String> convert() throws IOException, XMLStreamException {
		Map<String, String> toc = new HashMap<String, String>();
		
		String line = null;

		int rows = 0;
		Header h = null;
		Line l = null;

//		if ((line = _in.readLine()) != null) {
//			//System.out.println(line);
//		}
		
		while ((line = _in.readLine()) != null) {
			++rows;
			line = line.trim();
			if (line.length() <= 0) {
				continue;
			}

			String desc= null;
			if (line.indexOf('\t') >= 0) {
				desc = line.substring(0, line.indexOf('\t'));
				
				l = new Line(line);
				
				String type = l.getCols().get(0);
				String code = l.getCols().get(1);
				
				if (!"folder".equals(code)) {
					toc.put(type, desc);
				}
			}
		}

		_in.close();
		
		return toc;
	}
}