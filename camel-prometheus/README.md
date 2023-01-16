# Create a docker network
docker network create springboot-camel-prometheus

# Create a docker image
docker build --tag=camel-prometheus:latest .

# Run the container image
docker run --name camel-prometheus -p 8080:8080 --net=springboot-camel camel-prometheus:latest

# Check in your browser if you have camel metrics on your prometheus endpoint
http://localhost:8080/actuator/prometheus

# Create a Prometheus instance in the same network
docker run --name prometheus -p 9090:9090 --net=springboot-camel -v $PWD/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus:v2.41.0

# Check in your browser if you have metrics in prometheus
http://localhost:9090

Search for for instance 'CamelExchangesInflight'

docker run --name grafana -p 3000:3000 --net=springboot-camel grafana/grafana:9.3.2