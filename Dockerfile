FROM centos:latest

MAINTAINER Eko Indarto <eko.indarto@dans.knaw.nl>

# Environment variables required for this build (do NOT change)
# -------------------------------------------------------------
#http://download.oracle.com/otn-pub/java/jdk/8u144-b01/e758a0de34e24606bca991d704f6dcbf/jdk-8u144-linux-x64.rpm?AuthParam=1507356212_a17292ccf0a7095744a5588d7dc51953
#e758a0de34e24606bca991d704f6dcbf

ENV JAVA_DOWNLOAD_VERSION 8u151
ENV BUILD_VERSION b12
ENV JAVA_DOWNLOAD_PATH e758a0de34e24606bca991d704f6dcbf
ENV JAVA_VERSION  jdk1.8.0_151

ENV DATAVERSEBRIDGE_HOME /usr/local/dataverse-bridge

RUN mkdir $DATAVERSEBRIDGE_HOME
COPY ./target/dbsrv.jar $DATAVERSEBRIDGE_HOME/

RUN yum update -y && \
    yum install -y wget && \
    yum clean all


# Downloading, Install and configure Oracle JDK 8
# -------------------------------------

RUN cd /usr/local && \
	wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_DOWNLOAD_VERSION-$BUILD_VERSION/$JAVA_DOWNLOAD_PATH/jdk-$JAVA_DOWNLOAD_VERSION-linux-x64.tar.gz" && \
	tar xzf jdk-$JAVA_DOWNLOAD_VERSION-linux-x64.tar.gz && \
	rm jdk-$JAVA_DOWNLOAD_VERSION-linux-x64.tar.gz

RUN alternatives --install /usr/bin/java java /usr/local/$JAVA_VERSION/bin/java 200000
RUN alternatives --install /usr/bin/javaws javaws /usr/local/$JAVA_VERSION4/bin/java/javaws 200000
RUN alternatives --install /usr/bin/javac javac /usr/local/$JAVA_VERSION/bin/java/javac 200000

#ENV JAVA_HOME /usr/local/$JAVA_VERSION

#RUN java -version && \
#    export JAVA_HOME=$JAVA_HOME && \
#    echo $JAVA_HOME


EXPOSE 8592


#ENTRYPOINT exec java -jar $DATAVERSEBRIDGE_HOME/dbsrv.jar
#CMD [ "sh", "-c", "echo", "hello indarto" ]



