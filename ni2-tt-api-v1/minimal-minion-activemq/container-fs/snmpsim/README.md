# MIB definitions and simulation


## introduction

This simulator use the static snmpsim simulator to simulate the mibs used in this project

The version of the sipsim simulator is 0.4 and is packaged as a docker image

* see https://github.com/tandrup/docker-snmpsim
* see https://hub.docker.com/r/tandrup/snmpsim/tags

This online documentation points to a later version than the 0.4 image but is still useful (the names of the commands have changed)
* https://www.ibm.com/support/pages/how-use-snmpsim-simulate-network-device-based-snmp-walk-file
* https://docs.lextudio.com/snmpsim/documentation/managing-simulation-data#

However the actual documentation to use is in the 0.4 branch of the github repository.
* https://github.com/etingof/snmpsim/blob/v0.4.7/docs/source/quickstart.rst

## current simulation

snmpsim determines which data to use by the community name of the SNMP request. 
The community name determines which file in the /usr/local/snmpsim/data/ directory is read

2 files are injected by docker-compose

adam.snmprec  is a snmprec configuration created from advantech-IOcommon_v1.00+B07.mib

monacofes.snmprec is a snmprec configuration created from FES-STATUS-MIB-modified.MIB

these could be replaced with simulations taken from snmp walks


## to create a new simulation file directly from the mibs and not from snmpwalk

Note in both cases you need to add the manditory mib 2 OIDs so tat OpenNMS can recognise the device type

### advantech (adam) (advantech-IOcommon_v1.00+B07.mib)
launch the docker image using docker compose and then open a shell 

```
cd minimal-minion-activemq

docker-compose up -d

docker-compose exec snmpsim bash
```

once in the image first use the pysnmp library to compose the mib files to the pysnmp.py format.
These are needed for the next step in generating the simulator

```
/usr/local/bin/mibdump.py

 mibdump.py --debug=all --destination-directory=/usr/local/snmpsim/pysnmp_mibs/      /usr/local/snmpsim/mibs/SNMPv2-CONF.mib
 mibdump.py --debug=all --destination-directory=/usr/local/snmpsim/pysnmp_mibs/      /usr/local/snmpsim/mibs/advantech-IOcommon_v1.00+B07.mib

ls /usr/local/snmpsim/pysnmp_mibs/
ADVANTECH-IO-COMMON-MIB.py

```
Now create the simulation data using snmpsim

```
mib2dev.py --output-file=/usr/local/snmpsim/data/adam.snmprec --pysnmp-mib-dir=/usr/local/snmpsim/pysnmp_mibs   --mib-module=ADVANTECH-IO-COMMON-MIB --table-size=1 --unsigned-range=1,8
```

you can copy the generated file out of the image using
```
docker-compose cp monaco_01:/usr/local/snmpsim/data/adam.snmprec .
```

The files are placed in the snmpsim/data directory

The simulation can be accessed using community string 'adam' which corresponds to the file name

Add the following snmp mib 2 data - sysObjID is most critical
`1.3.6.1.2.1.1.2.0` sysObjID
`1.3.6.1.2.1.1.3.0` uptime timeticks
`1.3.6.1.2.1.1.7.0|2|0` sysServices integer 0-255

Add the following snmp mib 2 data to the top of the file

```
1.3.6.1.2.1.1.1.0|4| system description - adam
1.3.6.1.2.1.1.2.0|6|.1.3.6.1.4.1.10297.101
1.3.6.1.2.1.1.3.0|67|121722922
1.3.6.1.2.1.1.4.0|4| sysContact
1.3.6.1.2.1.1.5.0|4| sysName adam advantech
1.3.6.1.2.1.1.6.0|4| sysLocation
1.3.6.1.2.1.1.7.0|2|0

### monaco mibs

The supplied FES-STATUS-MIB.MIB cannot be used to generate data because the  DateAndTime (OCTET STRING) (SIZE (8 |11)). Hint: 2d-1d-1d,1d:1d:1d.1d,1a1d:1d from SNMPv2-TC is not interpreted correctly. 
The FES-STATUS-MIB-modified.MIB is used where SYNTAX DateAndTime  is replaced with SYNTAX (OCTET STRING) (SIZE (8 |11)) in two places
acuPrimaryTimeTag AND acuBackupTimeTag 

```
mibdump.py --debug=all --destination-directory=/usr/local/snmpsim/pysnmp_mibs/      /usr/local/snmpsim/mibs/FES-STATUS-MIB-modified.MIB

ls /usr/local/snmpsim/pysnmp_mibs/
FES-STATUS-MIB.py

mib2dev.py --output-file=/usr/local/snmpsim/data/monacofes.snmprec --pysnmp-mib-dir=/usr/local/snmpsim/pysnmp_mibs   --mib-module=FES-STATUS-MIB --table-size=1 --unsigned-range=1,8
```

you can copy the generated file out of the image using

```
docker-compose cp monaco_01:/usr/local/snmpsim/data/monacofes.snmprec .
```

The files are placed in the snmpsim/data directory

The simulation can be accessed using community string 'monacofes' which corresponds to the file name
   
Add the following snmp mib 2 data - sysObjID is most critical
`1.3.6.1.2.1.1.2.0` sysObjID
`1.3.6.1.2.1.1.3.0` uptime timeticks
`1.3.6.1.2.1.1.7.0|2|0` sysServices integer 0-255

Add the following snmp mib 2 data to the top of the file

```
1.3.6.1.2.1.1.1.0|4| system description - monaco fes
1.3.6.1.2.1.1.2.0|6|1.3.6.1.4.1.9633.7
1.3.6.1.2.1.1.3.0|67|121722922
1.3.6.1.2.1.1.4.0|4| sysContact
1.3.6.1.2.1.1.5.0|4| sysName monaco fes
1.3.6.1.2.1.1.6.0|4| sysLocation
1.3.6.1.2.1.1.7.0|2|0
```

