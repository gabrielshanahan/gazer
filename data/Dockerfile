FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=./data/build/libs/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
ARG wfi_args
ENV wfi_args_env=$wfi_args
COPY ./scripts/wait_for_it.sh ./
RUN ["chmod", "+x", "./wait_for_it.sh"]
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ../wait_for_it.sh $wfi_args_env -- java org.springframework.boot.loader.JarLauncher