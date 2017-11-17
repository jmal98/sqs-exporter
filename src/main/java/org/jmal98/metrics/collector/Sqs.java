package org.jmal98.metrics.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ListQueuesResult;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

public class Sqs extends Collector {

	private final Logger logger = LogManager.getLogger(getClass());

	private AmazonSQS sqs = null;
	
	private List<String> attributeNames = new ArrayList<String>();
	private Map<String,String> attributeDescriptions = new HashMap<String,String>();

	@Override
	public List<MetricFamilySamples> collect() {
		List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();

		try {

			if (sqs == null) {
				sqs = AmazonSQSClientBuilder.defaultClient();
			}

			ListQueuesResult queues = sqs.listQueues();
			for (String qUrl : queues.getQueueUrls()) {
				String[] tokens = qUrl.split("\\/");
				String queueName = tokens[tokens.length - 1];

				GetQueueAttributesResult attr = sqs.getQueueAttributes(qUrl, attributeNames);
				Map<String, String> qAttributes = attr.getAttributes();

				for (String key : qAttributes.keySet()) {
					GaugeMetricFamily labeledGauge = new GaugeMetricFamily(
							String.format("sqs_%s", key.toLowerCase().trim()),
							attributeDescriptions.get(key),
							Arrays.asList("queue"));

					labeledGauge.addMetric(Arrays.asList(queueName),
							Double.valueOf(qAttributes.get(key)));

					mfs.add(labeledGauge);
				}
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
		attributeDescriptions.put("ApproximateNumberOfMessages", "The approximate number of visible messages in a queue.");

		attributeNames.add("ApproximateNumberOfMessagesDelayed");
		attributeDescriptions.put("ApproximateNumberOfMessagesDelayed", "The approximate number of messages that are waiting to be added to the queue.");

		attributeNames.add("ApproximateNumberOfMessagesNotVisible");
		attributeDescriptions.put("ApproximateNumberOfMessagesNotVisible", "The approximate number of messages that have not timed-out and aren't deleted.");
	}

}
