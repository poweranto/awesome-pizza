
# Awesome pizza API
Awesome pizza é un progetto dimostrativo, sviluppato in Java, che espone alcune API HTTP

## Requisiti
Come "Awesome Pizza," voglio creare un nuovo portale per gestire gli ordini dei clienti. Il portale non richiede la registrazione dell'utente per effettuare un ordine. Il pizzaiolo vede la coda degli ordini e può prendere in carico un ordine alla volta. Quando la pizza è pronta, il pizzaiolo passa all'ordine successivo. L'utente riceve il codice del proprio ordine e può monitorarne lo stato fino al completamento.

## Stack tecnologico

*   Spring Boot - versione 3.5.5
*   Maven - come build/compile/test tool
*   JPA + Hibernate - come layer di persistenza dati
*   MySql 8 - come database engine


## HTTP API

I seguenti endpoint restituiscono dati in formato JSON.
In caso di malfunzionamenti/errori, restituiscono una risposta conforme allo standard [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html)

### GET /pizzas
Restituisce l'elenco delle pizze disponibili.

**Response**
```json
[
  {
    "id": 1,
    "name": "Margherita",
    "description": "pomodoro, mozzarella e basilico"
  }, {
    "id": 2,
    "name": "Marinara",
    "description": "pomodoro, aglio e origano"
  }
]
```

### GET /orders
Restituisce tutti gli ordini, ordinati in base alla data di creazione

**Response**
```json
[
  {
    "id": 22,
    "code": "2E659F9A",
    "status": "COMPLETED",
    "pizza": {
      "id": 255,
      "name": "Quattro Formaggi",
      "description": "mozzarella, gorgonzola, fontina e parmigiano"
    },
    "createdAt": "2025-09-08 14:59:34",
    "updatedAt": "2025-09-08 15:07:30"
  },
  {
    "id": 33,
    "code": "51451A26",
    "status": "IN_PROGRESS",
    "pizza": {
      "id": 254,
      "name": "Quattro Stagioni",
      "description": "pomodoro, mozzarella, funghi, prosciutto cotto, carciofini e olive"
    },
    "createdAt": "2025-09-08 15:04:05",
    "updatedAt": "2025-09-08 15:07:50"
  },
  {
    "id": 44,
    "code": "108DA384",
    "status": "PENDING",
    "pizza": {
      "id": 253,
      "name": "Marinara",
      "description": "pomodoro, aglio e origano"
    },
    "createdAt": "2025-09-08 15:04:06",
    "updatedAt": "2025-09-08 15:07:51"
  }
]
```

### POST /orders/pizza/:id
Effettua un ordine per la pizza con un determinato id e restituisce l'ordine.

**Response**
```json
{
    "id": 164,
    "code": "208FEB27",
    "status": "PENDING",
    "pizza": {
        "id": 257,
        "name": "Diavola",
        "description": "pomodoro, mozzarella e salame piccante"
    },
    "createdAt": "2025-09-08 17:40:23",
    "updatedAt": "2025-09-08 17:40:23"
}
```

### POST /orders/next
Imposta a `IN_PROGRESS` lo stato del prossimo ordine da processare (il più vecchio in stato `PENDING`) e restituisce l'ordine

**Response**
```json
{
    "id": 164,
    "code": "208FEB27",
    "status": "IN_PROGRESS",
    "pizza": {
        "id": 257,
        "name": "Diavola",
        "description": "pomodoro, mozzarella e salame piccante"
    },
    "createdAt": "2025-09-08 17:40:23",
    "updatedAt": "2025-09-08 17:50:23"
}
```

### POST /orders/:id/complete
Completa l'ordine modificandone lo stato da `IN_PROGRESS` a `COMPLETED` e restituisce l'ordine

**Response**
```json
{
    "id": 164,
    "code": "208FEB27",
    "status": "COMPLETED",
    "pizza": {
        "id": 257,
        "name": "Diavola",
        "description": "pomodoro, mozzarella e salame piccante"
    },
    "createdAt": "2025-09-08 17:40:23",
    "updatedAt": "2025-09-08 18:05:23"
}
```

## Avvio dell' applicazione
L’applicazione può essere eseguita in locale in diversi modi.

### Maven wrapper CLI
Il progetto include Maven wrapper (`mvnw`), che permette di avviare l’applicazione senza dover installare Maven a livello di sistema.

- Assicurati di avere installato e configurato correttamente **Java 17+.**
- Assicurati di avere un istanza **MySql 8+** in esecuzione
- Crea il database con nome `database`

Per avviare l’applicazione, eseguire il seguente comando dalla cartella di root del progetto
```bash
./mvnw spring-boot:run
```
Lo schema del database verrà creato in automatico.
L’applicazione partirà in locale sulla porta configurata (default: http://localhost:8080).

Per verificare il corretto funzionamento, aprire il browser e visitare l' URL
[http://localhost:8080/pizzas](http://localhost:8080/pizzas).
Verrà visualizzato l'elenco delle pizze in formato JSON



### Spring Tool Suite / Eclipse IDE

- Assicurati di avere un istanza **MySql 8+** in esecuzione
- Crea il database con nome `database`


- Importa il progetto come Maven Project
- Attendi che tutte le dipendenze vengano scaricate.
- Click destro sulla classe annotata con @SpringBootApplication (`com.awesomepizza.orderservice.OrderServiceApplication`) → Run As → Spring Boot App.

Lo schema del database verrà creato in automatico.

L’applicazione partirà in locale sulla porta configurata (default: http://localhost:8080).

Per verificare il corretto funzionamento, aprire il browser e visitare l' URL
[http://localhost:8080/pizzas](http://localhost:8080/pizzas).
Verrà visualizzato l'elenco delle pizze in formato JSON

### Docker
Il progetto include la configurazione necessaria per eseguire l'applicazione all'interno di container Docker (per lo sviluppo).
Da terminale, eseguire i seguenti passaggi, in ordine

1.
```bash
# da eseguire solo la prima
docker network create awesomepizza-net
```
Essendo il frontend in un progetto separato e dovendo comunicare via API con i servizi esposti da questo backend,
è necessario creare prima una rete docker esterna, affinché i container possano utilizzarla anche da progetti diversi.

2. inserire l' host `awesomepizza.demo.localhost` nel file hosts della macchina.

3. dalla root del progetto
```bash
cd .docker
docker compose up
```
Questo comando crea
- un container con il proxy nginx
- un container con l'istanza di MySql 8 e il database applicativo
- un container per il backend con Java e applicazione Spring Boot compilata

La prima volta, il comando impiega qualche minuto per avviare il tutto. 
Al termine, aprendo il browser e visitando indirizzo [awesomepizza.demo.localhost/api/pizzas](awesomepizza.demo.localhost/api/pizzas)
verrà visualizzato l'elenco delle pizze in formato JSON


## Test dell' applicazione
Sono presenti alcuni test di unità (unit tests) che coprono le principali logiche applicative. Di seguito le classi testate

- `com.awesomepizza.orderservice.service.OrderService`
- `com.awesomepizza.orderservice.controller.OrdersController`

### Spring Tool Suite / Eclipse IDE
Click destro sulla cartella root del progetto → Run As → JUnit Test.

### Maven wrapper CLI
Aprire il terminale ed eseguire il seguente comando dalla root del progetto
```bash
./mvnw test
```
Per testare una singola classe
```bash
/mvnw test -Dtest=OrdersControllerTest
```
Per testare un singolo metodo
```bash
/mvnw test -Dtest=OrdersControllerTest#shouldCreateAndReturnAnOrderIfPizzaExists
```

## @TODO/evolutive
- aggiungere maven profile per lo sviluppo
- includere libreria per il log (Log4j)
- aggiungere profilazione utenti (pizzaiolo, cliente)
- Introdurre un security layer (Spring Security) per proteggere API amministrative (ad esempio quelle che permettono di prendere in carico l'ordine e completarlo)