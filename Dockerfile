FROM alpine:3.6

RUN apk add --no-cache openjdk8-jre

RUN addgroup -g 9232 -S exporter ; \
        adduser -D -S -u 9232 -G exporter exporter

USER exporter

ADD target/exporter.jar exporter.jar

EXPOSE 9384

CMD ["java", "-Xmx512m", "-jar", "exporter.jar"]        
