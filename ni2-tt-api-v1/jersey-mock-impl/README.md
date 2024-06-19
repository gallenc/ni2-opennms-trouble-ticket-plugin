# Ni2 Trouble Ticket Mock Server

This module provides a simple mock trouble ticket server which emulates the ReST interface of Ni2 Trouble Ticket API.
It is used to do integration testing of the client module when the real server is not available. 

The code uses jersey and runs in Jetty. 

It is based off the example code in [jersey 3.0.14 jersey-heroku-webapp](https://github.com/eclipse-ee4j/jersey/blob/3.0.14/archetypes/jersey-heroku-webapp/src/main/resources/archetype-resources/pom.xml)

The model used in this server is the same model as is used by the TT api client.

