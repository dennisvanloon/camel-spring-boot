# Create a docker network
docker network create springboot-camel

# Create a Postgres database
docker run --name postgres-container -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=activemq_db -p 5432:5432 --net=springboot-camel -d postgres

# Create an ActiveMQ instance using the database
docker run --name activemq-container  -p 61616:61616 -p 8161:8161 -v $PWD/activemq/conf/activemq.xml:/opt/activemq/conf/activemq.xml -v $PWD/activemq/lib/commons-dbcp2-2.9.0.jar:/opt/activemq/lib/commons-dbcp2-2.9.0.jar -v $PWD/activemq/lib/postgresql-42.5.1.jar:/opt/activemq/lib/postgresql-42.5.1.jar --net=springboot-camel -d rmohr/activemq:5.15.9-alpine

