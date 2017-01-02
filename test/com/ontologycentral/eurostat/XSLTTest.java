package com.ontologycentral.eurostat;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

public class XSLTTest extends TestCase {

		public void testXSLT() throws Exception {
			Source xsltSource = new StreamSource("http://people.aifb.kit.edu/aha/2013/d3/xsl/sparql2kml-vessel.xsl");
			TransformerFactory transFact = TransformerFactory.newInstance();
			Templates cachedXSLT = transFact.newTemplates(xsltSource);
			Transformer trans = cachedXSLT.newTransformer();
			
			StreamSource ssource = new StreamSource("http://km.aifb.kit.edu/services/data-fu/ais/select");
			StreamResult sresult = new StreamResult(System.out);

			trans.transform(ssource, sresult);
		}
}
