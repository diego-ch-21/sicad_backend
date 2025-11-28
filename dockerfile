FROM eclipse-temurin:21

LABEL author=sicad.com

COPY target/sicad_backend-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]