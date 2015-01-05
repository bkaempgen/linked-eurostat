package com.ontologycentral.estatwrap.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@SuppressWarnings("serial")
public class FeedServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());
	
	static SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd.MM.yyyy");
	static SimpleDateFormat DATE_ISO = new SimpleDateFormat("yyyy-MM-dd");

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

		resp.setContentType("application/rdf+xml");
//		resp.setContentType("text/html");

		//OutputStream os = resp.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os , "UTF-8");
		PrintWriter pw = resp.getWriter();

		ServletContext ctx = getServletContext();
		
		Map<String, String> toc = (Map<String, String>)ctx.getAttribute(Listener.TOC);

		URL url = new URL(Listener.URI_PREFIX + "?file=table_of_contents_en.txt");
			
		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			InputStream is = conn.getInputStream();

			if (conn.getResponseCode() != 200) {
				resp.sendError(conn.getResponseCode());
			}

			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = "ISO-8859-1";
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));

			resp.setHeader("Cache-Control", "public");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, 10);
			resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));
			//resp.setHeader("Content-Encoding", "gzip");
			
			Map<String, Date> map = new HashMap<String, Date>();
			
			String line = null;
			while ((line = in.readLine()) != null) {
				StringTokenizer tok = new StringTokenizer(line, "\t");
				String code = null;
				String date = null;
				
				if (tok.hasMoreTokens()) {
					tok.nextToken();
					if (tok.hasMoreTokens()) { 
						code = tok.nextToken();
						if (tok.hasMoreTokens()) {
							if ("dataset".equals(tok.nextToken().trim())) {
								if (tok.hasMoreTokens()) {
									date = tok.nextToken();
								}
							}
						}
					}
				}
				
				if (code != null && date != null) { // && date.trim().length() != 0) {
					if (date.trim().length() > 0) {
						try {
							Date d = DATE_FMT.parse(date);
							map.put(code, d);
						} catch (ParseException e) {
							e.printStackTrace();
							_log.info(e.getMessage());
						}
					}
				}
			}
			
			Comparator dc =  new DateComparator(map);
	        TreeMap<String,Date> sortedMap = new TreeMap<String, Date>(dc);

	        sortedMap.putAll(map);
	        
	        Calendar monthago = Calendar.getInstance();
	        monthago.add(Calendar.MONTH, -1);
	        
	        Calendar change = Calendar.getInstance();
	        
			XMLOutputFactory factory = XMLOutputFactory.newInstance();

			try {
				XMLStreamWriter ch;

				ch = factory.createXMLStreamWriter(pw);

				ch.writeStartDocument("utf-8", "1.0");

				ch.writeStartElement("rdf:RDF");
				ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
				ch.writeDefaultNamespace("http://purl.org/rss/1.0/");
				ch.writeNamespace("dc", "http://purl.org/dc/elements/1.1/");
				
				ch.writeStartElement("channel");
				ch.writeAttribute("rdf:about", "http://estatwrap.ontologycentral.com/");

				ch.writeStartElement("title");
				ch.writeCharacters("Linked Eurostat");
				ch.writeEndElement();
				
				ch.writeStartElement("description");
				ch.writeCharacters("Recently updated Eurostat datasets");
				ch.writeEndElement();
				
				ch.writeStartElement("items");
				ch.writeStartElement("rdf:Seq");

		        for (String key : sortedMap.keySet()) {
		        	Date d = sortedMap.get(key);
		        	
		        	change.setTime(d);
		        	
		        	if (change.after(monthago)) {
		        		ch.writeStartElement("rdf:li");
		        		ch.writeAttribute("rdf:resource", "http://estatwrap.ontologycentral.com/id/" + key);
		        		ch.writeEndElement();
		        		//"'>" + toc.get(key) + "</a>: " + DATE_ISO.format(d) + "</li>").getBytes());
		        	}
		        }
				ch.writeEndElement();
				ch.writeEndElement();
		        
				// channel
				ch.writeEndElement();
				
		        for (String key : sortedMap.keySet()) {
		        	Date d = sortedMap.get(key);
		        	
		        	change.setTime(d);
		        	
		        	if (change.after(monthago)) {
		        		ch.writeStartElement("item");
		        		ch.writeAttribute("rdf:about", "http://estatwrap.ontologycentral.com/id/" + key);

		        		ch.writeStartElement("link");
		        		ch.writeCharacters("http://estatwrap.ontologycentral.com/id/" + key);
		        		ch.writeEndElement();

		        		ch.writeStartElement("title");
		        		ch.writeCharacters(toc.get(key));
		        		ch.writeEndElement();
		        		
		        		ch.writeStartElement("dc:date");
		        		ch.writeCharacters(DATE_ISO.format(d));
		        		ch.writeEndElement();

		        		ch.writeEndElement();
		        	}
		        }

				// rdf:RDF
				ch.writeEndElement();
				
				ch.writeEndDocument();

				ch.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
				_log.severe(e.getMessage());
			}

//	        os.write("<h1>Recently updated datasets</h1>".getBytes());
//	        os.write("<ul>".getBytes());
//	        for (String key : sortedMap.keySet()) {
//	        	Date d = sortedMap.get(key);
//	        	
//	        	change.setTime(d);
//	        	
//	        	if (change.after(monthago)) {
//	        		os.write(("<li><a href='/id/" + key + "'>" + toc.get(key) + "</a>: " + DATE_ISO.format(d) + "</li>").getBytes());
//	        	}
//	        }
//	        os.write("</ul>".getBytes());

	        //os.write(sortedMap.toString().getBytes()); //(code + " " + date + "\n").getBytes()); 

		} catch (IOException e) {
			resp.sendError(500, url + ": " + e.getMessage());
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			resp.sendError(500, url + ": " + e.getMessage());
			e.printStackTrace();
			return;			
		}
	}
}

class DateComparator implements Comparator {
	Map _base;
	public DateComparator(Map base) {
		_base = base;
	}

	public int compare(Object o1, Object o2) {
		Date d1 = (Date)_base.get(o1);
		Date d2 = (Date)_base.get(o2);
		
		if (d1.getYear() != d2.getYear()) 
			return d2.getYear() - d1.getYear();
		if (d1.getMonth() != d2.getMonth()) 
			return d2.getMonth() - d1.getMonth();
		return d2.getDate() - d1.getDate();
	}
}

