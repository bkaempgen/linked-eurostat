package com.ontologycentral.estatwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class IdentifierServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

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

		resp.setContentType("text/html");
		
		OutputStream os = resp.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os , "UTF-8");

		String id = req.getRequestURI();
		if (id == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		id = id.substring("/id/".length());
		
		ServletContext ctx = getServletContext();
		
		Map<String, String> toc = (Map<String, String>)ctx.getAttribute(Listener.TOC);
		
		String accept = req.getHeader("accept");
		
		if (toc.containsKey(id)) {
			if (accept != null && accept.contains("application/rdf+xml")) {
				//out.println(path + ".rdf");
				resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
				resp.setHeader("Location", "../data/" + id);
				return;
			} else {
				//out.println(path + ".html");
				resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
				resp.setHeader("Location", "../page/" + id);
				return;
			}
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}
}
