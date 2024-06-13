### Kafka Debug

start spring boot:

```shell
./gradlew clean bootRun
```

trigger controller:

```shell
curl --request POST --location 'http://localhost:8080/demo/kafka?event=toto'
```
