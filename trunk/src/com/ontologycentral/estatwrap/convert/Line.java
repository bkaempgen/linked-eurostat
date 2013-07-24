package com.ontologycentral.estatwrap.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class Line {
	List<String> _dim1 = new ArrayList<String>();
	List<String> _cols;

	public Line(String line)
	{
		List<String> cols = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(line, "\t");
		while (st.hasMoreTokens()) {
			cols.add(st.nextToken().trim());
		}

		String legend = (String)cols.get(0);

		_cols = cols.subList(1, cols.size());

		st = new StringTokenizer(legend, ",");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken().trim();
			_dim1.add(tok);
		}
	}

	public List<String> getCols() {
		return _cols;
	}

	public List<String> getDim1() {
		return _dim1;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(_dim1);
		sb.append(":");
		sb.append(_cols);

		return sb.toString();
	}
}
