Dataverse Bridge to push datasets from Dataverse temporary repository to TDR

Available options are:
- DANS EASY 
- Islandora
- Archivematica


#create container for dataverse bridge using postgres:latest images
#Postgres username: bridgeuser, password: Am@l1n, database: bridgedb
docker run --name dvn-bridge-postgres -d -e POSTGRES_USER=bridgeuser -e POSTGRES_PASSWORD=Am@l1n -e POSTGRES_DB=bridgedb --restart unless-stopped postgres:latest

#check the ip address of dvn-bridge-postgres, this ip address is needed to put in application.properties
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' dvn-bridge-postgres

#Build dataverse-bridge image
docker build -t dans/dataverse-bridge .

#create dbsrv container using dans/dataverse-bridge image and connect with dvn-bridge-postgres container 
docker run -p 8592:8592 -d --privileged=true -it --link dvn-bridge-postgres:dbp --name=dbsrv dans/dataverse-bridge

#access to dbsrv container
docker exec -it dbsrv java -jar /usr/local/dataverse-bridge/dbsrv.jar
