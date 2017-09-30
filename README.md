# Amazon Simple Queue Service (SQS) Exporter

A Prometheus exporter for the Amazon Simple Queue Service metrics.

### Build
```bash
mvn clean install
```

### Configuration
AWS credentials can be provided via the following:

* Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
* Credentials delivered through the Amazon EC2 container service if AWS_CONTAINER_CREDENTIALS_RELATIVE_URI" environment variable is set and security manager has permission to access the variable,
* Instance profile credentials delivered through the Amazon EC2 metadata service if running within AWS


### Docker Image

A Docker image can be created automatically by running the following which will perform the docker build (i.e. sudo docker build ....)

```bash
mvn clean install -P docker-image
```

## Running the Exporter

You can deploy this exporter using the produced Docker image.  If you run in AWS, the following will assume it's running with an IAM profile which will allow read access to SQS information.

```bash
docker run -d -p 9384:9384 sqs-exporter
```

If you would like to run the exporter with supplied environment configuration, the following will work both inside and outside of AWS.  This is useful if you desire to run the exporter externally.

```bash
docker run -d -p 9384:9384 -e AWS_ACCESS_KEY_ID=<access key> -e AWS_SECRET_ACCESS_KEY=<secret key> sqs-exporter
```

## Docker

You can deploy this exporter using the [jmal98/sqs-exporter](https://hub.docker.com/r/jmal98/sqs-exporter/) Docker image.

For example if using an IAM profile:

```bash
docker run -d -p 9384:9384 jmal98/sqs-exporter
```

For example if supplying environment configuration:

```bash
docker run -d -p 9384:9384 -e AWS_ACCESS_KEY_ID=<access key> -e AWS_SECRET_ACCESS_KEY=<secret key>  jmal98/sqs-exporter
```



### Exported Metrics

| Metric  | Labels |
| ------  | ------ |
| sqs_ATTRIBUTE_NAME | Queue Name |



* ApproximateNumberOfMessages - Approximate number of visible messages in a queue. For more information, see Resources Required to Process Messages in the Amazon SQS Developer Guide.
* ApproximateNumberOfMessagesDelayed - Approximate number of messages that are waiting to be added to the queue.
* ApproximateNumberOfMessagesNotVisible - Approximate number of messages that have not timed-out and aren't deleted. For more information, see Resources Required to Process Messages in the Amazon SQS Developer Guide.
