

# Trap Description

### sample received unformatted trap (with no configuration added)
```
.1.3.6.1.4.1.303.3.3.9.45.1.1.1.0="43019" .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0="Acquisition timeout for specified Cal Band" .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0="5" .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0="788" .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0="CalBandId=4" .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0="2024/06/15 23:50:03" .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0="Time Acquistion Time out after500 frames."

Here is a "mask" element definition that matches this event, for use in event configuration files:

    <mask>
      <maskelement>
        <mename>id</mename>
        <mevalue>.1.3.6.1.4.1.303.3.3.9.45.1.2</mevalue>
      </maskelement>
      <maskelement>
        <mename>generic</mename>
        <mevalue>6</mevalue>
      </maskelement>
      <maskelement>
        <mename>specific</mename>
        <mevalue>2</mevalue>
      </maskelement>
    </mask> 
```

This event will be generated by the following test trap

```
snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 5 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'
```

Fields :

```
sedMncAlarmTrap oid .1.3.6.1.4.1.303.3.3.9.45.1.2.2

%parm[#1]% alarmId  .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0

%parm[#2]% alarmDescription .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0

%parm[#3]% alarmSeverity .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0

%parm[#4]% reportingEntityID .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0

%parm[#5]% faultyEntityID .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0

%parm[#6]% timestamp .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0

%parm[#7]% supportingData .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0
```



# tests

## prerequisites

1. start opennms 

```
cd minimal-minion-activemq
docker compose up -d
```

2. load the node configurations from pris

```
cd minimal-minion-activemq

docker compose exec horizon /usr/share/opennms/bin/send-event.pl uei.opennms.org/internal/importer/reloadImport -p 'url http://pris:8000/requisitions/ni2ttTest1'
```

3 Send the following traps from the sbas container (172.20.0.114)

```
cd minimal-minion-activemq
docker compose exec sbas bash
```

```

a) critical .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 1

snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 1 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'

b) major .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 2

snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 2 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'

c) minor .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 3

snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 3 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'

d) warning .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 4

snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 4 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'


e) cleared .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 5

snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 5 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'

f) information .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 6

snmptrap -v 2c -c public horizon:1162 ""  .1.3.6.1.4.1.303.3.3.9.45.1.2.2    .1.3.6.1.4.1.303.3.3.9.45.1.1.1.0 s 43019   .1.3.6.1.4.1.303.3.3.9.45.1.1.2.0 s 'Acquisition timeout for specified Cal Band'  .1.3.6.1.4.1.303.3.3.9.45.1.1.3.0 i 6 .1.3.6.1.4.1.303.3.3.9.45.1.1.4.0 s 788 .1.3.6.1.4.1.303.3.3.9.45.1.1.5.0 s 'CalBandId=4' .1.3.6.1.4.1.303.3.3.9.45.1.1.6.0 s '2024/06/15 23:50:03' .1.3.6.1.4.1.303.3.3.9.45.1.1.7.0 s 'Time Acquistion Time out after500 frames.'

```

