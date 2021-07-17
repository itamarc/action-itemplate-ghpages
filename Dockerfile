#
# Build stage
#
FROM maven:3.8.1-openjdk-16-slim AS build
COPY pom.xml /home/app/
COPY entrypoint.sh /home/app/
COPY themes /home/app/themes
COPY src /home/app/src
RUN mvn -f /home/app/pom.xml package
RUN cp /home/app/target/action-itemplate-ghpages-*.jar /home/app/action-itemplate-ghpages.jar

#
# Package stage
#
FROM openjdk:16-slim
COPY --from=build /home/app/action-itemplate-ghpages.jar /usr/local/lib/action-itemplate-ghpages.jar
COPY --from=build /home/app/entrypoint.sh /usr/local/bin/
COPY --from=build /home/app/themes /opt/action-itemplate-ghpages/themes
# git is needed for the publish process
RUN apt-get update && apt-get -y install git
RUN chmod +x /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
