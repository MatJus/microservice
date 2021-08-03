# microservice
* JDK 8+
* Node >= 12
## Getting started 
Create project
```
git clone https://github.com/MatJus/microservice.git
```
## Backend - Vert.x

To package application:
```
./mvnw clean package
```
To run application:
```
./mvnw clean compile exec:java
```
### Example data
login or register JSON:
```json5
user: { login: <username>, password: <password> }
        
user: { login: "Mateusz", password: "12345" }
```
new item JSON:
```json5
item: { name: <item_name> }
        
item: { name: "Item1" }
```
## Frontend - React
To build application:
```
yarn install
```
To run application:
```
yarn start
```

Simple microservice in Vert.x for register/login user and add/get items after autorization
