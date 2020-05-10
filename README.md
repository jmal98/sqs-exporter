# Amazon Simple Queue Service (SQS) Exporter

A Prometheus exporter for the Amazon Simple Queue Service metrics.

### Build
```bash
docker build --tag sqs-exporter  .
```

### Exported Metrics

| Metric  | Labels |
| ------  | ------ |
| sqs_ATTRIBUTE_NAME | Queue Name |



* ApproximateNumberOfMessages - Approximate number of visible messages in a queue. For more information, see Resources Required to Process Messages in the Amazon SQS Developer Guide.
* ApproximateNumberOfMessagesDelayed - Approximate number of messages that are waiting to be added to the queue.
* ApproximateNumberOfMessagesNotVisible - Approximate number of messages that have not timed-out and aren't deleted. For more information, see Resources Required to Process Messages in the Amazon SQS Developer Guide.

### Configuration
AWS credentials can be provided via the following:

* Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
* Credentials delivered through the Amazon EC2 container service if AWS_CONTAINER_CREDENTIALS_RELATIVE_URI" environment variable is set and security manager has permission to access the variable,
* Instance profile credentials delivered through the Amazon EC2 metadata service if running within AWS
* IAM role applied to either an Amazon Elastic Container Service (ECS) service or task
* Custom SQS Endpoint: You can define the environment variable `SQS_ENDPOINT` to access a compatible SQS Service (i.e. http://localhost:4100)

By default, the exporter will watch all SQS queues visible to the AWS account. To watch a specific set of queues, supply one of these parameters:
* a comma-separated list of queue names in the environment variable `SQS_QUEUE_NAMES`;
* a single queue prefix in `SQS_QUEUE_NAME_PREFIX`. (SQS_QUEUE_NAMES takes precedence over SQS_QUEUE_NAME_PREFIX);
* a comma-separated list of queue URLs in `SQS_QUEUE_URLS`.

With no parameters specified via environment variables, the full list of queues will be returned.

## Docker

You can deploy this exporter using the [jmal98/sqs-exporter](https://hub.docker.com/r/jmal98/sqs-exporter/) Docker image.

If you run in AWS, the following will assume it's running with an IAM profile which will allow read access to SQS information.

```bash
docker run -d -p 9384:9384 jmal98/sqs-exporter:0.0.7
```

Use the following to pass the queue filtering environment variables to the docker container.

```bash
docker run -d -e SQS_QUEUE_NAME_PREFIX='example_queue_prefix' -p 9384:9384 jmal98/sqs-exporter:0.0.7
```

If you would like to run the exporter with supplied environment configuration, the following will work both inside and outside of AWS.  This is useful if you desire to run the exporter externally.

```bash
docker run -d -p 9384:9384 -e AWS_ACCESS_KEY_ID=<access key> -e AWS_SECRET_ACCESS_KEY=<secret key> -e AWS_REGION=<region>  jmal98/sqs-exporter:0.0.7
```
