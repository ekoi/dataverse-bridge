Dataverse Bridge to push datasets from Dataverse temporary repository to TDR

Available options are:
- DANS EASY 
- Islandora
- Archivematica



#Build dataverse-bridge image
docker build -t dans/dataverse-bridge .

#create dbsrv container using dans/dataverse-bridge image and connect with dvn-bridge-postgres container 
docker run -p 8592:8592 -d --privileged=true -it --name=dbsrv dans/dataverse-bridge

#access to dbsrv container
docker exec -it dbsrv bash

#access to dbsrv container and start it
ocker exec -it dbsrv java -jar /usr/local/dataverse-bridge/dbsrv.jar

# To detach the tty without exiting the shell,
# use the escape sequence Ctrl-p + Ctrl-q
# note: This will continue to exist in a stopped state once exited (see "docker ps -a")

#The ip address of dbsrv
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' dbsrv

