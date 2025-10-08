mvn clean

mvm package

#java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar build/lib/reative-flushcards-1.0.0.jar
mvn spring-boot:run