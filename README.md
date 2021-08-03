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
###Example data
login or register JSON:
```json 
user: { login: <username>, password: <password> }
```
new item JSON:
```json 
item: { name: <item name> }
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
