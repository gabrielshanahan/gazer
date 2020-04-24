FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG MODULE
COPY ./$MODULE/build/libs/*-boot.jar application.jar
RUN ["mkdir", "dependencies", "spring-boot-loader", "snapshot-dependencies", "application"]
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
ARG WFI_ARGS
ENV WFI_ARGS_ENV=$WFI_ARGS
COPY ./scripts/wait_for_it.sh ./
RUN ["chmod", "+x", "./wait_for_it.sh"]
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ../wait_for_it.sh $WFI_ARGS_ENV -- java -Dspring.profiles.active=prod org.springframework.boot.loader.JarLauncher