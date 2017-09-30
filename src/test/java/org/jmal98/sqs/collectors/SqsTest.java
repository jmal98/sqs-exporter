package org.jmal98.sqs.collectors;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.jmal98.metrics.collector.Sqs;
import org.junit.BeforeClass;
import org.junit.Test;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.exporter.common.TextFormat;

public class SqsTest {

	private CollectorRegistry registry = CollectorRegistry.defaultRegistry;

	private Sqs collector = new Sqs();

	@BeforeClass
	public static void setup() {
		assumeTrue(System.getenv("AWS_ACCESS_KEY_ID") != null);
	}

	@Test
	public void collect() throws IOException {

		collector.register();

		List<MetricFamilySamples> metrics = collector.collect();
		assertNotNull(metrics);
		assertTrue(metrics.size() > 0);

		StringWriter writer = new StringWriter();
		TextFormat.write004(writer, registry.metricFamilySamples());
		assertNotNull(writer.toString());
		System.out.println(writer);

	}

}
