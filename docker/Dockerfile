FROM centos:centos6
MAINTAINER Diffeo <support@diffeo.com>

RUN yum install -y epel-release \
 && yum update -y \
 && yum clean all

# Update the system to current packages;
RUN yum install -y \
      ant \
      ant-apache-regexp \
      ivy \
      java-1.8.0-openjdk \
      java-1.8.0-openjdk-devel \
      java-util \
      tar \
      unzip \
      wget \
      zip \
 && yum clean all

# Get a tagged release of OpenSextantToolbox
ENV OST_VERSION=2.2.6 GAZDATA_VERSION=20141229 OST_COMMIT=661b14d928c3313a6c3be4905727f38c145df19f

RUN wget -O ost.tar.gz https://github.com/OpenSextant/OpenSextantToolbox/archive/${OST_COMMIT}.tar.gz \
 && tar xzf ost.tar.gz \
 && rm ost.tar.gz

# ant is broken with open jdk 1.8.
# We need to patch `build-classpath` and create some directories.
# ADD ant-openjdk-1.8.0.patch /
# RUN cd / \
# && yum install -y patch \
# && patch -p1 < ant-openjdk-1.8.0.patch \
# && mkdir /usr/share/java-1.8.0 \
# && mkdir /usr/lib/java-1.8.0

# Make ant detect the right Java version.
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk.x86_64
RUN test -d $JAVA_HOME

RUN cd OpenSextantToolbox-${OST_COMMIT}/ \
  && mkdir LanguageResources/resources/geoGazetteer/ 
  && wget https://github.com/OpenSextant/Gazetteer/releases/download/v20160717/opensextant-gazetteer.zip 
  && unzip opensextant-gazetteer.zip -d LanguageResources/resources/geoGazetteer/ \
  && ls -lah LanguageResources/resources/geoGazetteer/ 
  && mkdir build \
  && mv opensextant-gazetteer.zip build/

# Build and unpack the release
# Note that the final zip file version includes a date stamp
RUN cd OpenSextantToolbox-${OST_COMMIT}/ \
  && ls -lah 
  && sed -ie 's/MergedGazetteer_SMALL.txt/MergedGazetteer.txt/g' build.xml 
  && ant -verbose release 
  && ls -lah
  && unzip opensextant-toolbox-3.0.zip 
  && ant clean

# Record build number
# COPY container-version /etc/

# Finally, run the OpenSextant tagger end point
EXPOSE 8182
WORKDIR OpenSextantToolbox-${OST_COMMIT}/opensextant-toolbox-3.0
CMD bash ./scripts/start.sh
