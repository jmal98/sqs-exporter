package org.jmal98.metrics.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
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
				String region = new DefaultAwsRegionProviderChain().getRegion();
				sqs = AmazonSQSClientBuilder
								.standard()
								.withRegion(region)
							.build();
				logger.info("AmazonSQS client is connected to region: ({})", region);
			}

			List<String> queueUrls;

			// check for manually-specified queue names
			String queueNames = System.getenv("SQS_QUEUE_NAMES");
			if (queueNames != null) {
			    // find the URLs for the named queues
			    String[] names = queueNames.split(",");
			    queueUrls = new ArrayList<String>();
			    for(String name : names) {
				queueUrls.add(sqs.getQueueUrl(name).getQueueUrl());
			    }
			} else {
			    // get URLs for all queues visible to this account
			    ListQueuesResult queues = sqs.listQueues();
			    queueUrls = queues.getQueueUrls();
			}

			for (String qUrl : queueUrls) {
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
		attributeDescriptions.put("ApproximateNumberOfMessagesDelayed", "TESTTESTTESTTESTTEST");
		
		attributeNames.add("ApproximateNumberOfMessagesNotVisible");
		attributeDescriptions.put("ApproximateNumberOfMessagesNotVisible", "The approximate number of messages that have not timed-out and aren't deleted.");
	}

}
