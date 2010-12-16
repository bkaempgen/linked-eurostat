package com.ontologycentral.estatwrap.convert;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

public class ToCTest extends TestCase {
	public void testData() throws Exception {
		URL u = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?sort=1&file=table_of_contents.txt");

		//URL u = new URL("http://europa.eu/estatref/download/everybody/table_of_contents.txt");

        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
        InputStream is = conn.getInputStream();

	    ToC toc = new ToC(is, null);		

	    Map<String, String> map = toc.convert();
	    
	    for (String key : map.keySet()) {
	    	//System.out.println(key + ": " + map.get(key));
	    	if (key.equals("tag00002")) {
	    		System.out.println("bingo");
	    	}
	    }
	}
}
