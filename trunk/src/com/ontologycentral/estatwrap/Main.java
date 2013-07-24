package com.ontologycentral.estatwrap;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.ontologycentral.estatwrap.convert.Data;
import com.ontologycentral.estatwrap.convert.DataPage;
import com.ontologycentral.estatwrap.convert.DictionaryPage;

public class Main {
	public static void main(String[] args) throws IOException, XMLStreamException {
		Options options = new Options();
		
		Option outputO = new Option("o", "name of file to write, - for stdout");
		outputO.setArgs(1);
		options.addOption(outputO);

		Option idO = new Option("i", "name of Eurostat id (e.g., tsieb010)");
		idO.setArgs(1);
		options.addOption(idO);

		Option dicO = new Option("d", "name of Eurostat dic (e.g., geo)");
		dicO.setArgs(1);
		options.addOption(dicO);
		
		Option helpO = new Option("h", "help", false, "print help");
		options.addOption(helpO);
		
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
			
			if (!(cmd.hasOption("i") || cmd.hasOption("d"))) {
				throw new ParseException("specify either -i or -d");
			}
		} catch (ParseException e) {
			System.err.println("***ERROR: " + e.getClass() + ": " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("parameters:", options );
			return;
		}
		
		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("parameters:", options );
			return;
		}

		PrintStream out = System.out;
		
		if (cmd.hasOption("o")) {
			if (cmd.getOptionValue("o").equals("-")) {
				out = System.out;
			} else {
				out = new PrintStream(new FileOutputStream(cmd.getOptionValue("o")));
			}
		}
		
		Data.MAX_COLS = Integer.MAX_VALUE;
		Data.MAX_ROWS = Integer.MAX_VALUE;
		
		String id = null;
		URL url = null;
		
		if (cmd.hasOption("i")) {
			id = cmd.getOptionValue("i");
			url = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=data/" + id + ".tsv.gz");
			//url = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=" + URLEncoder.encode("data/" + id + ".tsv.gz", "utf-8"));
		} else if (cmd.hasOption("d")) {
			id = cmd.getOptionValue("d");
			url = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=dic/en/" + id + ".dic");
		}

		System.out.println(url);

		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		InputStream is = null;
				
		if (url.toString().endsWith("gz")) {
			//is = conn.getInputStream();			
			is = new GZIPInputStream(conn.getInputStream());
		} else {
			is = conn.getInputStream();			
		}
		if (conn.getResponseCode() != 200) {
			throw new IOException("Response code: " + conn.getResponseCode());
		}

		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));

	    XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		XMLStreamWriter ch = factory.createXMLStreamWriter(out, "utf-8");

		if (cmd.hasOption("i")) {
			DataPage.convert(ch, new HashMap<String, String>(), id, in);
		} else if (cmd.hasOption("d")) {
			//DictionaryPage.convert(ch, id, in);
		}

		ch.close();

		out.close();
	}

}
