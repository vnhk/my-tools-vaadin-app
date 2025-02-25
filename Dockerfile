FROM maven:3.9.2-eclipse-temurin-17 as builder

WORKDIR /app

COPY --from=bervan-utils /app/core.jar /app/core.jar
COPY --from=bervan-utils /app/history-tables-core.jar /app/history-tables-core.jar
COPY --from=bervan-utils /app/ie-entities.jar /app/ie-entities.jar

RUN mvn install:install-file -Dfile=./core.jar -DgroupId=com.bervan -DartifactId=core -Dversion=latest -Dpackaging=jar -DgeneratePom=true
RUN mvn install:install-file -Dfile=./history-tables-core.jar -DgroupId=com.bervan -DartifactId=history-tables-core -Dversion=latest -Dpackaging=jar -DgeneratePom=true
RUN mvn install:install-file -Dfile=./ie-entities.jar -DgroupId=com.bervan -DartifactId=ie-entities -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=common-vaadin /app/common-vaadin.jar /app/common-vaadin.jar
RUN mvn install:install-file -Dfile=./common-vaadin.jar -DgroupId=com.bervan -DartifactId=common-vaadin -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=file-storage-app /app/file-storage-app.jar /app/file-storage-app.jar
RUN mvn install:install-file -Dfile=./file-storage-app.jar -DgroupId=com.bervan -DartifactId=file-storage-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=pocket-app /app/pocket-app.jar /app/pocket-app.jar
RUN mvn install:install-file -Dfile=./pocket-app.jar -DgroupId=com.bervan -DartifactId=pocket-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=spreadsheet-app /app/spreadsheet-app.jar /app/spreadsheet-app.jar
RUN mvn install:install-file -Dfile=./spreadsheet-app.jar -DgroupId=com.bervan -DartifactId=spreadsheet-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=project-mgmt-app /app/project-mgmt-app.jar /app/project-mgmt-app.jar
RUN mvn install:install-file -Dfile=./project-mgmt-app.jar -DgroupId=com.bervan -DartifactId=project-mgmt-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=canvas-app /app/canvas-app.jar /app/canvas-app.jar
RUN mvn install:install-file -Dfile=./canvas-app.jar -DgroupId=com.bervan -DartifactId=canvas-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=streaming-platform-app /app/streaming-platform-app.jar /app/streaming-platform-app.jar
RUN mvn install:install-file -Dfile=./streaming-platform-app.jar -DgroupId=com.bervan -DartifactId=streaming-platform-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=interview-app /app/interview-app.jar /app/interview-app.jar
RUN mvn install:install-file -Dfile=./interview-app.jar -DgroupId=com.bervan -DartifactId=interview-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=english-text-stats-app /app/english-text-stats-app.jar /app/english-text-stats-app.jar
RUN mvn install:install-file -Dfile=./english-text-stats-app.jar -DgroupId=com.bervan -DartifactId=english-text-stats-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=learning-language-app /app/learning-language-app.jar /app/learning-language-app.jar
RUN mvn install:install-file -Dfile=./learning-language-app.jar -DgroupId=com.bervan -DartifactId=learning-language-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY --from=shopping-stats-server-app /app/shopping-stats-server-app.jar /app/shopping-stats-server-app.jar
RUN mvn install:install-file -Dfile=./shopping-stats-server-app.jar -DgroupId=com.bervan -DartifactId=shopping-stats-server-app -Dversion=latest -Dpackaging=jar -DgeneratePom=true

COPY /pom.xml ./pom.xml
COPY /tsconfig.json ./tsconfig.json
COPY /types.d.ts ./types.d.ts
COPY /vite.config.ts ./vite.config.ts
COPY /src/main/java ./src/main/java
COPY /src/main/resources ./src/main/resources
COPY /src/main/frontend/themes ./src/main/frontend/themes
COPY /src/main/frontend/index.html ./src/main/frontend/index.html
COPY /src/main/frontend/theme-changer.js ./src/main/frontend/theme-changer.js
COPY /configuration ./configuration

#COPY . .
RUN mvn clean vaadin:prepare-frontend
RUN mvn install -Pproduction -DskipTests -U

FROM openjdk:17 AS runtime

COPY --from=builder /app/target/my-tools-vaadin-app.jar ./my-tools-vaadin-app.jar
COPY --from=builder /app/configuration/ ./configuration/

CMD ["java", "-jar", "-Dspring.profiles.active=production", "my-tools-vaadin-app.jar"]