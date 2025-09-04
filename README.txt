
[API - Host: localhost:8080]
POST /orders/pizza/:id 
to order specific pizza

GET /pizzas
retrieve all pizzas

GET /orders
retrieve all orders, ordered by creation date asc

POST /orders/next
process next order, setting IN_PROGRESS status

POST /orders/:id/complete
complete IN_PROGRESS order, setting COMPLETE status


[run application]
run application using maven wrapper CLI. It requires a JDK installed
exec this command from the project root
./mvnw spring-boot:run

Con Docker
- eseguire il comando "docker compose up" dalla cartella .docker
api: awesomepizza.demo.localhost/api
app: awesomepizza.demo.localhost
inserire l'host nel file hosts della macchina


[test application]
./mvnw test

[test single method]
./mvnw clean test -Dtest=com.awesomepizza.orderservice.OrdersControllerTest#testMethodName

[packages testati]
Sono test unitari sulle logiche di servizio più il controller per gli ordini
service Layer - order service
web Layer - order controller

