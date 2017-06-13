package com.ontologycentral.estatwrap.convert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.ontologycentral.estatwrap.Main;

/**
 * Test handling of encoding.
 * 
 * @author aharth
 */
public class TestEncoding {

	@Test
	public void test() throws Exception {
		String lang = "en";
		String id = "cities";

		URL url = new URL(Main.URI_PREFIX + "?file=dic/" + lang + "/" + id + ".dic");

		System.out.println("looking up " + url);

		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		InputStream is = conn.getInputStream();

		if (conn.getResponseCode() != 200) {
			System.err.println("Error: " + conn.getResponseCode());
			return;
		}

		String encoding = conn.getContentEncoding();

		System.err.println("Encoding: " + encoding);
		
		if (encoding == null) {
			encoding = "UTF-8";
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));
		String l;
		StringBuilder sb = new StringBuilder();

		while ((l = in.readLine()) != null) {
			sb.append(l);
			sb.append('\n');
		}
		in.close();

		String str = sb.toString();
		System.out.println(str);
	}
}
