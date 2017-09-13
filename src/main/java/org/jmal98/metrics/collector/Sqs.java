package org.jmal98.metrics.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmal98.sqs.exporter.Application;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ListQueuesResult;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

public class Sqs extends Collector {
	
	private final Logger logger = LogManager.getLogger(getClass());

	private AmazonSQSClient sqs = null;

	private List<String> attributeNames = new ArrayList<String>();

	@Override
	public List<MetricFamilySamples> collect() {
		List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();

		try {
			
			if (sqs == null) {
				sqs = new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
			}

			ListQueuesResult queues = sqs.listQueues();
			for (String qUrl : queues.getQueueUrls()) {
				String[] tokens = qUrl.split("\\/");
				String queueName = tokens[tokens.length - 1];

				GaugeMetricFamily labeledGauge = new GaugeMetricFamily(String.format("aws_sqs_queue_%s", queueName),
						"help", 
						Arrays.asList("attribute"));

				GetQueueAttributesResult attr = sqs.getQueueAttributes(qUrl, attributeNames);
				Map<String, String> qAttributes = attr.getAttributes();

				for (String key : qAttributes.keySet()) {
					labeledGauge.addMetric(Arrays.asList(key.toLowerCase().trim()),
							Double.valueOf(qAttributes.get(key)));
				}
				mfs.add(labeledGauge);
			}

		} catch (AmazonClientException e) {
			logger.error(e.getMessage());
			if (sqs != null)
				sqs.shutdown();
			sqs = null; // force reconnect
		}

		return mfs;
	}

	public Sqs() {
		super();
		attributeNames.add("ApproximateNumberOfMessages");
		attributeNames.add("ApproximateNumberOfMessagesDelayed");
		attributeNames.add("ApproximateNumberOfMessagesNotVisible");
	}

}
