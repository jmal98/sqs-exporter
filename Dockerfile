FROM alpine:3.7

RUN apk add --no-cache openjdk8-jre

ENV MAVEN_VERSION=3.8.3 \
    MAVEN_SHA512=1c12a5df43421795054874fd54bb8b37d242949133b5bf6052a063a13a93f13a20e6e9dae2b3d85b9c7034ec977bbc2b6e7f66832182b9c863711d78bfe60faa

RUN addgroup -g 9232 -S exporter ; \
        adduser -D -S -u 9232 -G exporter exporter

ADD src /tmp/src

ADD pom.xml /tmp/pom.xml

RUN apk --update add --no-cache --virtual build-dependencies openjdk8 curl \
  && curl -SL -o /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" \
  && echo -n "$MAVEN_SHA512  /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz" | sha512sum -c \
  && tar -xzvf /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz -C /tmp \
  && /tmp/apache-maven-$MAVEN_VERSION/bin/mvn -f /tmp/pom.xml clean install -B \
  && mv /tmp/target/exporter.jar /. \
  && chown exporter:exporter /exporter.jar \
  && rm -rf /tmp/* /root/.m2 \
  && apk del build-dependencies

USER exporter

EXPOSE 9384

CMD ["java", "-Xmx512m", "-jar", "exporter.jar"]
