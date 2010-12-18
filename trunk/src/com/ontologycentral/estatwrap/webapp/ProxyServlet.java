package com.ontologycentral.estatwrap.webapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ProxyServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		BufferedInputStream webin = null;
		BufferedOutputStream out = null;

		HttpURLConnection con;
		//HttpURLConnection.setFollowRedirects(false);

		int status;

		String q = req.getParameter("query");
		String a = req.getParameter("accept");

		URL uri = new URL("http://q.ontologycentral.com/sparql?query=" + URLEncoder.encode(q, "utf-8") + "&accept=" + URLEncoder.encode(a, "utf-8"));

		_log.info("Fetching :" + uri.toString());

		con = (HttpURLConnection)uri.openConnection();
		con.setConnectTimeout(20*1000);
		con.setReadTimeout(20*1000);

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.addRequestProperty("Cache-control", "public");

		for (Enumeration<String> e = req.getHeaderNames() ; e.hasMoreElements();) {
			String name = e.nextElement().toString();
			con.setRequestProperty(name, req.getHeader(name));
		}

		con.connect();

		status = con.getResponseCode();
		resp.setStatus(status);

		for (Iterator i = con.getHeaderFields().entrySet().iterator() ; i.hasNext() ;) {
			Map.Entry mapEntry = (Map.Entry)i.next();
			if(mapEntry.getKey() != null) {
				resp.setHeader(mapEntry.getKey().toString(), ((List)mapEntry.getValue()).get(0).toString());
			}
		}

		webin = new BufferedInputStream(con.getInputStream());
		out = new BufferedOutputStream(resp.getOutputStream());

		int onebyte;

		while ((onebyte = webin.read()) != -1) {
			out.write(onebyte);
		}

		out.flush();
		out.close();

		webin.close();
		con.disconnect();
	}
}
