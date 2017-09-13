package org.jmal98.sqs.collectors;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jmal98.metrics.collector.Sqs;
import org.junit.BeforeClass;
import org.junit.Test;

import io.prometheus.client.Collector.MetricFamilySamples;

public class SqsTest {

	@BeforeClass
	public static void setup() {
		assumeTrue(System.getenv("AWS_ACCESS_KEY_ID") != null);
	}

	@Test
	public void collect() {
		Sqs collector = new Sqs();
		List<MetricFamilySamples> metrics = collector.collect();
		assertNotNull(metrics);
		assertTrue(metrics.size() > 0);
	}

}
