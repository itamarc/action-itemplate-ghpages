#
# Build stage
#
FROM maven:3.8.1-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
COPY src/main/resources/* /home/app/templates/
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
## If the version is changed in pom.xml you need to update here...
ENV JARVERSION="0.1"
COPY --from=build /home/app/target/action-itemplate-ghpages-${JARVERSION}.jar /usr/local/lib/action-itemplate-ghpages-${JARVERSION}.jar
COPY --from=build /home/app/templates/* /home/app/templates/
RUN mkdir /home/app/doc
## ...and here, since ENTRYPOINT does not allow ENV variables
ENTRYPOINT ["java","-jar","/usr/local/lib/action-itemplate-ghpages-0.1.jar","/home/app/templates","/home/app/doc"]
