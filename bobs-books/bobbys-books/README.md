# Book store app

Book store app has two parts right now: 

* Helidon MP Book API application
* React web front end


## Helidon MicroProfile Books API 

### To Build and Run

From the root directory: 

```
mvn clean install
cd helidon-mp
java -Dbookstore.size=5 -jar target/helidon-mp.jar
```

### REST API

|Verb|Path|Description
|----|----|-----------|
|GET|/books|Returns list of books|
|POST|/books|Adds a new book|
|GET|/books/{isbn}|Returns book with the given isbn number|
|PUT|/books/{isbn}|Updates book with the given isbn number|
|DELETE|/books/{isbn}|Deletes book with the given isbn number|

### Sample JSON

See `helidon-mp/src/test/book.json`

### Example Curl Commands

```bash
curl -H "Content-Type: application/json" \
 -X POST http://localhost:8080/books \
 --data @helidon-mp/target/test-classes/book.json 
 
curl -H 'Accept: application/json' -X GET http://localhost:8080/books

curl -H 'Accept: application/json' -X GET http://localhost:8080/books/123456

curl -H "Content-Type: application/json" \
 -X PUT http://localhost:8080/books/1234 \
 --data @helidon-mp/target/test-classes/book.json 
 
curl -X DELETE http://localhost:8080/books/123456
```

## React web front end

### To build and run

First make sure you have node (including npm) and yarn installed, then:

```
cd web
npm install 
yarn start
```

This will start up the development server and open a browser at http://localhost:3000/home
where you will see the web app. 

You can fiddle with the bookstore.size parameter on the Helidon app to get more/less books. 
There must be at least one or the web app will barf.  It's just a demo! 

![demo](demo.png)