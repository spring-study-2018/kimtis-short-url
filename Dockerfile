FROM java:openjdk-8-jdk

WORKDIR /kimtis/program/bootapp
ADD build/libs/app.jar app.jar

CMD ["/bin/bash", "-c", "exec java -Dspring.profiles.active=production $JAVA_OPTS -jar app.jar >> /kimtis/logs/bootapp/app.log 2>&1"]

# docker run --name=redis -d -p 6379:6379 redis:latest
# docker run --name=mysql -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 mysql:5.6
# docker run --name=app -d -p 80:8080 -e JAVA_OPTS="-Dshort-url.service.home-page={HOST_IP}" kimtis/short-url/app:latest
