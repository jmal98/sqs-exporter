package org.jmal98.sqs.exporter;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {

	private static final long serialVersionUID = 313995012804498559L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (
				Writer writer = resp.getWriter();
			){
			writer.write("<html>SQS Exporter</html>");
			writer.flush();
		} finally {}
	}

}

