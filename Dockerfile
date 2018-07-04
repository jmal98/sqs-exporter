FROM alpine:3.7

RUN apk add --no-cache openjdk8-jre

ENV MAVEN_VERSION=3.5.4 \
    MAVEN_SHA1=22cac91b3557586bb1eba326f2f7727543ff15e3

RUN addgroup -g 9232 -S exporter ; \
        adduser -D -S -u 9232 -G exporter exporter

ADD src /tmp/src

ADD pom.xml /tmp/pom.xml

RUN apk --update add --no-cache --virtual build-dependencies openjdk8 curl \
  && curl -SL -o /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" \
  && echo -n "$MAVEN_SHA1  /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz" | sha1sum -c \
  && tar -xzvf /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz -C /tmp \
  && /tmp/apache-maven-$MAVEN_VERSION/bin/mvn -f /tmp/pom.xml clean install -B \
  && mv /tmp/target/exporter.jar /. \
  && chown exporter:exporter /exporter.jar \
  && rm -rf /tmp/* /root/.m2 \
  && apk del build-dependencies

USER exporter

EXPOSE 9384

CMD ["java", "-Xmx512m", "-jar", "exporter.jar"]
