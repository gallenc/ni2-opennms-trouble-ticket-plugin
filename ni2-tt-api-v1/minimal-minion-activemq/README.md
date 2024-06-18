# test environment for ni2 trouble ticket plugin

A full build of the project will place the kar file in the correct docker compose directory corresponding to OpenNMS_home/deploy.

```
cd ni2-tt-api-v1
mvn clean install
```
to run the plugin start the docker compose project

```
cd ni2-tt-api-v1/minimal-minon-activemq

docker compose up -d

```

once horizon is running SSH into the Karaf shell from inside the horizon instance and install the plugin

```

docker compose exec horizon bash

ssh -p 8101 admin@localhost

feature:install ni2-ticketing

```

Alternatively edit the `etc/org.apache.karaf.features.cfg` file and add the featuresBoot entry to include the ni2-ticketing feature by editing the file as shown below.

```
# OPENNMS: Parenthesize standard Karaf boot features, add OpenNMS product features
featuresBoot = ( \
...
  scv-shell, \
  ni2-ticketing, \
  opennms-karaf-health
# Ensure that the 'opennms-karaf-health' feature is installed *last*
# C GALLEN ADDED ni2-ticketing so ni2 ticketer starts automatically

```

## karaf commands to test plugin
a number of commands are provided to allow testing of the plugin

| command | details |
| -------- | ------- |
| ni2ticket --help | lists help for commands |
| ni2ticket:create-remote-ticket |Create a new ticket in remote system. (Note - this does not link a ticket to an alarm in OpenNMS) |
| ni2ticket:change-status  | change status of ticket |
| ni2ticket:get-auth-token | get authentication token |
| ni2ticket:get-ticket     | get trouble ticket |


## to reinstall kar manually. 

```
cd minimal-minion-activemq

docker compose cp ../ni2-tt-api-v1/assembly/kar/target/ni2-tt-api-v1-kar-0.0.1-SNAPSHOT.kar  horizon:/usr/share/opennms/deploy/

```

## test provisioning

A [Pris](https://docs.opennms.com/pris/2.1.0/index.html) instance is provided which can be used to generate a requisition from a spreadsheet or csv file.

The current example uses ni2terrestar.csv as the requisition.

To view this requisition outside the docker compose project, browse to http://localhost:8000/requisitions/ni2terrestar

To load the requisition from inside the running OpenNMS use

```
docker compose exec horizon /usr/share/opennms/bin/send-event.pl uei.opennms.org/internal/importer/reloadImport -p 'url http://pris:8000/requisitions/ni2ttTest1' 
```

You will also need to have snmp community strings in `snmp-config.xml`  corresponding to the type of the snmpsim confgurations.

OpenNMS should provision and begin scanning the nodes.
