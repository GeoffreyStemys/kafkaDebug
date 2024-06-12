You can avoid running constantly the docker compose when iterating in tests with the following

Do once:
```shell
docker compose up -d
```

Then use the `-PwithDockerCompose=false` flag in any gradle cmd:
(you can also modify the "run configuration" in the IDE)
```shell
./gradlew clean build -PwithDockerCompose=false
```

Clean all with: 
```shell
docker compose down -v
```

```shell
docker compose ps
```

```shell
docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
```

### Kafka

Follow:
https://medium.com/@tetianaokhotnik/setting-up-a-local-kafka-environment-in-kraft-mode-with-docker-compose-and-bitnami-image-enhanced-29a2dcabf2a9

http://localhost:8081/ui/

Server is docker compose hostname (`kafka` here) port is the 9094 

To run the tests like in CI:

```shell
docker exec -it ubuntu /bin/bash
```
