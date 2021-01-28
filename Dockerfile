FROM maven:3.6.3-openjdk-14-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -B package --file pom.xml -DskipTests

FROM tomcat:8.5.41-jre8-alpine
COPY --from=build /workspace/target/mystok.war /usr/local/tomcat/webapps/
COPY ./conf/server.xml /usr/local/tomcat/conf/
COPY ./ROOT/index.jsp /usr/local/tomcat/webapps/ROOT/
COPY ./lib/*.jar /usr/local/tomcat/lib/
EXPOSE 80
