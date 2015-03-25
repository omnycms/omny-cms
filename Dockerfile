FROM ubuntu:14.04

EXPOSE 8080
RUN apt-get update
RUN apt-get install unzip zip openjdk-7-jdk maven wget -y -q
ADD . /tmp/src 
RUN cd /tmp/src && bash build.sh && bash bundle.sh 1
RUN mkdir -p /opt/omny/ && cd /opt/omny && unzip /tmp/src/output/omny-1.zip 
CMD ["java","-jar","/opt/omny/AllInOne-1.0.jar"]
