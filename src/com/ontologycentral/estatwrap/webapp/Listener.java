package com.ontologycentral.estatwrap.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import com.ontologycentral.estatwrap.convert.ToC;

public class Listener implements ServletContextListener {
	public static SimpleDateFormat RFC822 = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	public static SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public static String FACTORY = "f";
	public static String TOC = "t";
	public static String CACHE = "c";
	
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();

	    XMLOutputFactory factory = XMLOutputFactory.newInstance();

	    ctx.setAttribute(FACTORY, factory);
	    
        Cache cache = null;

        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
    		ctx.setAttribute(CACHE, cache);
        } catch (CacheException e) {
        	e.printStackTrace();
        }

	    Map<String, String> map = null;
	    
	    if (cache.containsKey(TOC)) {
	    	map = (Map<String, String>)cache.get(TOC);
	    }

	    if (map == null) {
	    	try {
	    		URL u = new URL("http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?sort=1&file=table_of_contents.txt");

	    		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
	    		InputStream is = conn.getInputStream();

	    		ToC toc = new ToC(is, null);		
	    		map = toc.convert();
	    		cache.put(TOC, map);
	    	} catch (MalformedURLException e) {
	    		e.printStackTrace();
	    		map = new HashMap<String, String>();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		map = new HashMap<String, String>();
	    	} catch (XMLStreamException e) {
	    		e.printStackTrace();
	    		map = new HashMap<String, String>();
	    	}
	    }
	    //map = new HashMap<String, String>();
	    ctx.setAttribute(TOC, map);	    
	}

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub		
	}
}
