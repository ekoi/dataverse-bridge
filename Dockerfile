FROM centos:latest

# Environment variables required for this build (do NOT change)
# -------------------------------------------------------------
#http://download.oracle.com/otn-pub/java/jdk/8u144-b01/090f390dda5b47b9b721c7dfaa008135/jdk-8u144-linux-x64.rpm?AuthParam=1507356212_a17292ccf0a7095744a5588d7dc51953
# 090f390dda5b47b9b721c7dfaa008135
#http://download.oracle.com/otn-pub/java/jdk/8u151-b12/e758a0de34e24606bca991d704f6dcbf/jdk-8u151-linux-x64.tar.gz
ENV JAVA_DOWNLOAD_VERSION 8u151
ENV BUILD_VERSION b12
ENV JAVA_DOWNLOAD_PATH e758a0de34e24606bca991d704f6dcbf
ENV JAVA_VERSION  jdk1.8.0_151

ENV DATAVERSEBRIDGE_GITHUB https://github.com/ekoi
ENV DATAVERSEBRIDGE_GITHUB_REPO dataverse-bridge
ENV DATAVERSEBRIDGE_HOME /usr/local/dvnbridge

ENV MAVEN_VERSION 3.5.0

RUN yum update -y && \
    yum install -y unzip && \
    yum install -y wget && \
    yum install -y git && \
    yum clean all


# Install and configure Oracle JDK 8
# -------------------------------------
# Downloading Java
RUN cd /usr/local && \
	wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_DOWNLOAD_VERSION-$BUILD_VERSION/$JAVA_DOWNLOAD_PATH/jdk-$JAVA_DOWNLOAD_VERSION-linux-x64.tar.gz" && \
	tar xzf jdk-$JAVA_DOWNLOAD_VERSION-linux-x64.tar.gz && rm jdk-$JAVA_DOWNLOAD_VERSION-linux-x64.tar.gz

RUN alternatives --install /usr/bin/java java /usr/local/$JAVA_VERSION/bin/java 200000 && ls -tral
RUN alternatives --install /usr/bin/javaws javaws /usr/local/$JAVA_VERSION4/bin/java/javaws 200000
RUN alternatives --install /usr/bin/javac javac /usr/local/$JAVA_VERSION/bin/java/javac 200000

ENV JAVA_HOME /usr/local/$JAVA_VERSION

RUN java -version && export JAVA_HOME=$JAVA_HOME && echo $JAVA_HOME

#DataverseBridge download and install

RUN useradd -d $DATAVERSEBRIDGE_HOME -m -s /bin/bash dvnbridge && echo dvnbridge:dvnbr1dg3 | chpasswd

#Downloading and install maven
RUN cd $DATAVERSEBRIDGE_HOME && wget http://apache.cs.uu.nl/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip && \
    unzip apache-maven-$MAVEN_VERSION-bin.zip


RUN cd $DATAVERSEBRIDGE_HOME && \
    git clone  $DATAVERSEBRIDGE_GITHUB/$DATAVERSEBRIDGE_GITHUB_REPO.git && \
	cd $DATAVERSEBRIDGE_GITHUB_REPO && \
	git fetch && git checkout -b dbsrv origin/dataverse-bridge-docker &&\
	$DATAVERSEBRIDGE_HOME/apache-maven-$MAVEN_VERSION/bin/mvn clean install

RUN chown -R dvnbridge:dvnbridge $DATAVERSEBRIDGE_HOME

RUN echo root:3k0inD|chpasswd

EXPOSE 8592

# Set glassfish user in its home/bin by default
USER dvnbridge

ENTRYPOINT exec java -jar /usr/local/dvnbridge/dataverse-bridge/target/dataverse-bridge-docker-1.0.0-SNAPSHOT.jar



