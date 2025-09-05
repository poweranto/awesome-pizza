
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


[run API application]
run application using maven wrapper CLI.
It requires a JDK installed
exec this command from the project root
./mvnw spring-boot:run


[Con Docker]

Essendo il frontend in un progetto separato e dovendo comunicare via API con i servizi esposti da questo backend,
è necessario creare prima una rete docker esterna.
La rete deve essere esterna affinché i container possano utilizzarla anche da progetti diversi

1. docker network create awesomepizza-net (va eseguito solamente una volta)
2. inserire l'host "awesomepizza.demo.localhost" nel file hosts della macchina (solo una volta)

3. docker compose up, dalla cartella .docker
 - viene creata un istanza MySql8
 - viene creato il backend - Java e applicazione Spring Boot compilata
 - viene creato un proxy nginx
 -- espone API - awesomepizza.demo.localhost/api
 -- espone FRONTEND - awesomepizza.demo.localhost
 - Questo per non avere problemi CORS
Al termine, aprendo il browser e visitando indirizzo awesomepizza.demo.localhost/api/pizzas
dovrebbe vedersi l'elenco delle pizze in formato JSON

 
4. docker compose up, dalla cartella del progetto frontend
Al termine, aprendo il browser e visitando indirizzo awesomepizza.demo.localhost
dovrebbe vedersi l'applicazione React di frontend


[test application]
./mvnw test

[test single method]
./mvnw clean test -Dtest=com.awesomepizza.orderservice.OrdersControllerTest#testMethodName

[packages testati]
Sono test unitari sulle logiche di servizio più il controller per gli ordini
service Layer - order service
web Layer - order controller

