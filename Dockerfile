FROM openjdk:11

COPY ./HomeWork-1/build/libs/HomeWork-1-0.0.1-SNAPSHOT.jar /

EXPOSE 5000 5000

ENV LC_ALL C.UTF-8
ENV MASTER_DB_HOST mysql-master.default.svc.cluster.local
ENV CLUSTER_DB_HOST mysql-reads.default.svc.cluster.local
ENV JDBC_DATABASE_URL jdbc:mysql://mysql-master.default.svc.cluster.local/db?reconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC", "-jar", "/HomeWork-1-0.0.1-SNAPSHOT.jar"]
