package com.ontologycentral.eurostat;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

public class UriTest extends TestCase {
	public void testUri() throws MalformedURLException {
		URL u = new URL("http://q.ontologycentral.com/sparql?query=PREFIX++sdmx-measure%3A+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fmeasure%23%3E%0D%0APREFIX++eus%3A++%3Chttp%3A%2F%2Fontologycentral.com%2F2009%2F01%2Feurostat%2Fns%23%3E%0D%0APREFIX++rdf%3A++%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX++qb%3A+++%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23%3E%0D%0A%0D%0ASELECT++%3Fvalue+%3Fgeo+%3Ftime%0D%0AFROM+%3Chttp%3A%2F%2Festatwrap.ontologycentral.com%2Fdata%2Fbsin_m%3E%0D%0AWHERE+{%0D%0A++++%3Fs+qb%3Adataset+%3Chttp%3A%2F%2Festatwrap.ontologycentral.com%2Fid%2Fbsin_m%23ds%3E+.%0D%0A++++%3Fs+eus%3Atime+%3Ftime+.%0D%0A++++%3Fs+eus%3Ageo+%3Fgeo+.%0D%0A++++%3Fs+sdmx-measure%3AobsValue+%3Fvalue+.%0D%0A++++%3Fs+eus%3Aindic+%3Chttp%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Findic%23BS-ISPE%3E+.%0D%0A++++%3Fs+eus%3As_adj+%3Chttp%3A%2F%2Festatwrap.ontologycentral.com%2Fdic%2Fs_adj%23SA%3E%0D%0A}%0D%0A&accept=application%2Fsparql-results%2Bjson");
	}
}
