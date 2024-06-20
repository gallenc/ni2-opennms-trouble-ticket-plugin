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

docker compose cp ../ni2-tt-api-v1/assembly/kar/target/ni2-tt-api-v1-kar-0.0.2-SNAPSHOT.kar  horizon:/usr/share/opennms/deploy/

```

## test provisioning

A [Pris](https://docs.opennms.com/pris/2.1.0/index.html) instance is provided which can be used to generate a requisition from a spreadsheet or csv file.

The current example uses ni2ttTest1.csv as the requisition.

To view this requisition outside the docker compose project, browse to http://localhost:8000/requisitions/ni2ttTest1

To load the requisition from inside the running OpenNMS use

```
docker compose exec horizon /usr/share/opennms/bin/send-event.pl uei.opennms.org/internal/importer/reloadImport -p 'url http://pris:8000/requisitions/ni2ttTest1' 
```

You will also need to have snmp community strings in `snmp-config.xml`  corresponding to the type of the snmpsim confgurations.

OpenNMS should provision and begin scanning the nodes.

## configuration modifications from pristine in this project

This configuration is based on OpenNMS 32.0.5

## ni2 plugin installation and enablement

The OpenNMS OSGi ticketing integration provides a generic mechanism for linking OpenNMS Alarms to trouble tickets.
A number of different ticketing integrations are provided and the newest integrations are provided as OSGi plugins. 
OSGi provides a flexible mechanism to install new features in OpenNMS without modifying the core code.
The Ni2 Trouble Ticketing plugin is implemented as an OSGi feature.

In order to install and enable the plugin changes need to be made in several places.

### deploy ni2-tt-api-v1-kar-x.x.x.kar

Firstly you should download the ni2-tt-api-v1-kar-XXX.kar file (where XXX is the current version number) from the github folder 

[ni2-opennms-trouble-ticket-plugin/releases](https://github.com/gallenc/ni2-opennms-trouble-ticket-plugin/releases)  

e.g. wget  https://github.com/gallenc/ni2-opennms-trouble-ticket-plugin/releases/download/v0.0.1/ni2-tt-api-v1-kar-0.0.1.kar

Install this file in the karaf deploy directory `-opennms-home-/deploy`

To automatically enable the ni2 plugin on startup, modify `-opennms-home-/etc/org.apache.karaf.features.cfg`

Add `ni2-ticketing` to the end of the featuresBoot property but before `opennms-karaf-health`

```
featuresBoot = ( \
  ...
  scv-shell, \
  ni2-ticketing, \
  opennms-karaf-health
# Ensure that the 'opennms-karaf-health' feature is installed *last*
```

## ni2 Plugin configuration

### Ni2 properties file

The main properties for the ni2 plugin are held in `-opennms-home-/etc/opennms.properties.d/ni2-troubleticket.properties` which should not be checked into git since it contains sensitive passwords.

Instead copy/rename the template and change the values accordingly `-opennms-home-/etc/opennms.properties.d/ni2-troubleticket.properties.template`

```
# This file contains configuration properties for the Ni2 Trouble Ticket Plugin
#
# RENAME or copy this file to ni2-troubleticket.properties and change values appropriately
# DO NOT check passwords into git

# url of the Ni2 trouble ticket server
ni2.tt.server.url=http://localhost:8080

# username to access api
ni2.tt.server.username=username

# password to access api
ni2.tt.server.password=password

# if true the client will trust all TLS/HTTPS certificates from server
ni2.tt.server.trustallcertificates=true

# identify of the OpenNMS system sending tickets to the server
ni2.tt.opennms.instance=OpenNMS1

# resource id to be used if not passed in ticket attributes from drools
ni2.tt.opennms.fallbackresource="set_fallback_resource";

# see OpenNMS documentation https://docs.opennms.com/horizon/33/operation/deep-dive/ticketing/introduction.html

# this tells the ticketing engine to look for an OSGi plugin exposing he ticketing interface. 
# Only one OSGi ticketing plugin can be active at a time and the system will use the first one to be registered.
# (Ensure you only have one OSGi ticketing feature installed at a time)
opennms.ticketer.plugin=org.opennms.netmgt.ticketd.OSGiBasedTicketerPlugin

opennms.alarmTroubleTicketEnabled=true

# note this should be set up to get the url linking to the ticket
opennms.alarmTroubleTicketLinkTemplate=http://localhost:8080/api/v1/entity/event/get/event/extended/${id}

# this sets up rules for mapping values in alarms and nodes to tickets
opennms.ticketer.servicelayer=org.opennms.netmgt.ticketd.DroolsTicketerServiceLayer

## note you must also set the rules file in  /etc/drools-ticketer.properties 
## drools-ticketer.rules-file=/opt/opennms/etc/ni2-drools-ticketer-rules.drl

```

Most of the properties in the example file are self explanatory but some require extra mention below.

| property | description |
| :-------  | :----------  |
|ni2.tt.opennms.instance | This tells Ni2 which instance of OpenNMS raised the ticket. It should be combined with the alarm id to give a unique reference for the alarm |
|ni2.tt.opennms.fallbackresource | Ni2 mst have a resource which it recognises referenced in every ticket. However some alarms in OpenNMS will not have a resource<BR>In this case, the fallback resource will be used. (probably this shouldl just reference the OpenNMS system itself) |
|opennms.alarmTroubleTicketLinkTemplate | This is used to create a HREF link which will redirect the user to the Ni2 ticket.|

Note that the properties set in this file are used as default values when karaf CLI commands are issued to test ticket generation. 


### Ni2 OSGi Drools Ticketer mapping

The DroolsTicketerServiceLayer uses a set of drools rools to map OpenNMS Alarm and Node information into an OpenNMS ticket.
The Ni2 plugin maps the OpenNMS ticktet to an ni2 trouble ticket.

The `-opennms-home-/etc/drools-ticketer.properties` file tells the DroolsTicketerServiceLayer where the drools rules file is stored

create a the file and add the following elements

```
#
# Drools Ticketer Configuration for ni2
#
drools-ticketer.rules-file=/opt/opennms/etc/ni2-drools-ticketer-rules.drl
```

Example rules are provided in

`-opennms-home-/etc/ni2-drools-ticketer-rules.drl`

```
package org.opennms.netmgt.ticketd;
import org.opennms.netmgt.model.OnmsAlarm;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsCategory;
import org.opennms.api.integration.ticketing.Ticket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

dialect "mvel"

global Ticket ticket;

/*
 * Set tickets defaults
*/

rule "TicketDefaults"
salience 100
 when
  $alarm : OnmsAlarm()
  $node  : OnmsNode()
  
 then
  // note bug - must set ticket.alarmId as not set by service
  ticket.alarmId = $alarm.id

  ticket.summary = $alarm.logMsg
  
  // keeps html in alarm description
  // ticket.details = $alarm.description
  
  // removes html tags but preserves line feeds
  ticket.details = Jsoup.clean($alarm.description, "", Whitelist.none(), new OutputSettings().prettyPrint(false));

  ticket.addAttribute("ni2.tt.resourceids", $node.label);
  
  // use for more exact resource id using foreign source and foreign id.
  // ticket.addAttribute("ni2.tt.resourceids", $node.foreignSource:$node.foreignId);

end


```
You can see that ticket information is extracted from the alarm and node information. 

In this example, we have simply mapped the alarm log message to the ticket.summary.

We have used Jsoup to remove all of the HTML tags from the alarm description and placed this in the ticket.details

OpennMS Tickets can contain an arbitrary number of attributes mapped as name value pairs. 

These are mapped into ni2 tickets as follows

| OpenNMS Ticket Attribute name | Ni2 Ticket mapping |
| :---------------------------- | :------------------- |
| ni2.tt.resourceids  | this should contain a comma separated list of Ni2 Resource ids to map to the ticket. The example just provides one resource, the node label |
| ni2.tt.attributes.XXX | this will be mapped to an arbirary ni2 ticket custom attribte, with the name set to the value of the text after attributes. (i.e. the XXX characters) |

It is possible to add more rules which can process alarms into tickets differently depending on the data in the alarm or node objects.

For information, the objects referenced in drools are

| drools reference | OpenNMS object (on github) |
| :---------------- | :-------------------------  |
| ticket           | [Ticket.java](https://github.com/OpenNMS/opennms/blob/opennms-32.0.5-1/features/ticketing/api/src/main/java/org/opennms/api/integration/ticketing/Ticket.java) |
| $alarm           | [OnmsAlarm.java](https://github.com/OpenNMS/opennms/blob/opennms-32.0.5-1/opennms-model/src/main/java/org/opennms/netmgt/model/OnmsAlarm.java)                           |
| $node           | [OnmsNode.java](https://github.com/OpenNMS/opennms/blob/opennms-32.0.5-1/opennms-model/src/main/java/org/opennms/netmgt/model/OnmsNode.java)  |



## logging ni2 plugin

Logging happens in two places for the Ni2 plugin. 

Firstly the generic OpenNMS ticketer code generates logs in `-opennms-home-/logs/trouble-tickter.log`

To see trouble-ticketer debug logs change `-opennms-home-/etc/log4j2.xml` adding a new routing appender and change trouble-ticketer from INFO to DEBUG

```
    <logger name="org.ni2.v01" additivity="false" level="DEBUG">
     <appender-ref ref="RoutingAppender"/>
    </logger>

    <!-- Allow any message to pass through the root logger -->
    <root level="DEBUG">
        ...
        <KeyValuePair key="trouble-ticketer"     value="DEBUG" />
        ...
```

Secondly the ni2 OSGi plugin generates logs in `-opennms-home-/logs/karaf.log`

However these logs can be directed to a separate file `-opennms-home-/logs/ni2-ticketing-plugin.log` by adding the following configuration to the end of `-opennms-home-/etc/org.ops4j.pax.logging.cfg`

```

# logger for ni2 plugins
log4j2.logger.ni2.name = org.ni2
log4j2.logger.ni2.level = DEBUG
log4j2.logger.ni2.additivity = true
log4j2.logger.ni2.appenderRef.Ni2PluginRollingFile.ref = Ni2PluginRollingFile

# Rolling file appender for ni2 plugins
log4j2.appender.plugin.type = RollingRandomAccessFile
log4j2.appender.plugin.name = Ni2PluginRollingFile
log4j2.appender.plugin.fileName = ${karaf.log}/ni2-ticketing-plugin.log
log4j2.appender.plugin.filePattern = ${karaf.log}/ni2-ticketing-plugin.log.%i
log4j2.appender.plugin.append = true
log4j2.appender.plugin.layout.type = PatternLayout
log4j2.appender.plugin.layout.pattern = ${log4j2.pattern}
log4j2.appender.plugin.policies.type = Policies
log4j2.appender.plugin.policies.size.type = SizeBasedTriggeringPolicy
log4j2.appender.plugin.policies.size.size = 16MB

```

### Pris Configuration

[OpenNMS Provisioning integration server](https://docs.opennms.com/pris/2.1.0/index.html) which loads OpenNMS configuration from a csv file

Requisition [ni2ttTest1.csv](../minimal-minion-activemq/container-fs/pris/requisitions/ni2ttTest1/ni2ttTest1.csv)
 holds the monitoring for a local test environment and OpenNMS itself
