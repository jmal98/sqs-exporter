package org.jmal98.sqs.exporter;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jmal98.metrics.collector.Sqs;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

public class Application {

	private static final Logger logger = LogManager.getLogger(Application.class);

	static final Counter startup = Counter.build()
			.name("exporter_startup_total")
			.help("Total starts.")
		.register();

	public static void main(String[] args) {
		Application app = new Application();
		try {
			app.go();
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(-1);
		}
	}

	private void go() throws Exception {
		DefaultExports.initialize();
		new Sqs().register();

		Server server = new Server(9384);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		handler.addServletWithMapping(IndexServlet.class, "/");
		handler.addServletWithMapping(MetricsServlet.class, "/metrics");
		handler.addFilterWithMapping(DisableMethodsFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

		server.start();

		logger.info("Exporter has started.");
		startup.inc();
		
		HttpGenerator.setJettyVersion("");

		server.join();

	}

}