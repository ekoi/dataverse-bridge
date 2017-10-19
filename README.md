Dataverse Bridge to push datasets from Dataverse temporary repository to TDR

Available options are:
- DANS EASY 
- Islandora
- Archivematica


docker run --name dvn-bridge -d -e POSTGRES_USER=dvnbridge -e POSTGRES_PASSWORD=qwerty postgres:latest
docker exec -it dvn-bridge bash
psql -U dvnbridge -h 172.17.0.5  -p 5432

docker build -t dans/dataverse-bridge .
docker run -p 8592:8592 -d --privileged=true -it --link dvn-bridge:dvn-bridge -e "POSTGRES_DATABASE=bridgedb" -e "POSTGRES_USER=bridgeuser" -e "POSTGRES_PASSWORD=secret" --expose=4848 --name=dvn-bridge-easy dans/dataverse-bridge
docker exec -it dvn-bridge-easy bash


mvn package && java -jar target/dataverse-bridge-docker-1.0.0-SNAPSHOT.jar
