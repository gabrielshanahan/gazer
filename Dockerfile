FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
COPY scripts/wait_for_it.sh scripts/run_java_jarlauncher.sh ./
RUN ["chmod", "+x", "./wait_for_it.sh", "./run_java_jarlauncher.sh"]
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["../run_java_jarlauncher.sh"]