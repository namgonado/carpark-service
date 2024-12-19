FROM eclipse-temurin:17.0.7_7-jre-alpine
WORKDIR /opt/app
RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN chown -R javauser:javauser .
USER javauser
ENTRYPOINT ["sh", "-c", "java -jar app.jar ${@}"]
