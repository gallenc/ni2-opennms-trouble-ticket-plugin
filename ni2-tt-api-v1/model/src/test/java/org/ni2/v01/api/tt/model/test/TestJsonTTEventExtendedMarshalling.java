/*
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */

package org.ni2.v01.api.tt.model.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ni2.v01.api.tt.model.AuthenticationRequest;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class TestJsonTTEventExtendedMarshalling {
   

   @Test
   public void testAuthenticationRequestObject() throws StreamWriteException, DatabindException, IOException {
      
      JsonMapper mapper = new JsonMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      
      AuthenticationRequest request = new AuthenticationRequest();
      request.setUsername("user1");
      request.setPassword("password1");
      
      StringWriter stringwriter = new StringWriter();
      mapper.writeValue(stringwriter, request);
      
      String reaquestStr = stringwriter.toString();
      System.out.println("AuthenticationRequest JSON is\n"+reaquestStr);
     
      AuthenticationRequest request2 = mapper.readValue(reaquestStr, AuthenticationRequest.class);
      System.out.println("unmarshalled AuthenticationRequest is\n"+request2.toString());
      
      assertTrue(request.toString().equals(request2.toString()));
      
   }
   
   String EXTENDED_EVENT_EXAMPLE1 = "{\r\n"
            + "    \"description\": \"something happened\",\r\n"
            + "    \"longDescription\": \"something happened a lot\",\r\n"
            + "    \"customAttributes\": {\r\n"
            + "        \"Description\": \"something happened\",\r\n"
            + "        \"CorrectedPendingTime\": 0,\r\n"
            + "        \"Category\": \"Network\",\r\n"
            + "        \"ResponseAgreementStatus\": \"In Compliance\",\r\n"
            + "        \"Escalation\": false,\r\n"
            + "        \"Expedite\": false,\r\n"
            + "        \"ActualStartDate\": 1715178133658,\r\n"
            + "        \"Knowledge\": false,\r\n"
            + "        \"Name\": \"EVT-00012395\",\r\n"
            + "        \"CorrectedResponseTime\": 0,\r\n"
            + "        \"Identifier\": \"EVT-00012395\",\r\n"
            + "        \"Submitter\": \"APIOpenNMS\",\r\n"
            + "        \"UnSqueezable\": false,\r\n"
            + "        \"RootCauseComment\": \"test\",\r\n"
            + "        \"ResponseTime\": 0,\r\n"
            + "        \"UniversalId\": \"EVT-00012395\",\r\n"
            + "        \"ResolutionDate\": 1715179491750,\r\n"
            + "        \"Status\": \"Resolved\",\r\n"
            + "        \"AddedStartPendingTime\": 0,\r\n"
            + "        \"PendingTime\": 0,\r\n"
            + "        \"LoanEquipment\": \"0 Loaned\",\r\n"
            + "        \"RequestSysMode\": \"new\",\r\n"
            + "        \"Priority\": \"3-Moderate\",\r\n"
            + "        \"RequiresAttention\": false,\r\n"
            + "        \"CorrectedResolutionTime\": 1,\r\n"
            + "        \"ResolutionAgreementStatus\": \"In Compliance\",\r\n"
            + "        \"ResolutionTime\": 1,\r\n"
            + "        \"LongDescription\": \"something happened a lot\",\r\n"
            + "        \"RootCause\": \"Software [Provider]\",\r\n"
            + "        \"LastModificationDate\": 1715179491887,\r\n"
            + "        \"AddedPendingTime\": 0\r\n"
            + "    },\r\n"
            + "    \"category\": \"Event\",\r\n"
            + "    \"classifications\": [\r\n"
            + "        \"Event\",\r\n"
            + "        \"Event\",\r\n"
            + "        \"Support\",\r\n"
            + "        \"Incident\",\r\n"
            + "        \"Monitoring Incident\"\r\n"
            + "    ],\r\n"
            + "    \"classificationPath\": \"Event(\\\"Event/Support/Incident/Monitoring Incident\\\")\",\r\n"
            + "    \"name\": \"EVT-00012395\",\r\n"
            + "    \"universalId\": \"EVT-00012395\",\r\n"
            + "    \"services\": [],\r\n"
            + "    \"resources\": [\r\n"
            + "        {\r\n"
            + "            \"customAttributes\": {\r\n"
            + "                \"Status\": \"In Process\",\r\n"
            + "                \"Identifier\": \"FUN-00002497\",\r\n"
            + "                \"NamingConvention\": \"oneTimeFree\",\r\n"
            + "                \"LastModificationDate\": 1716936428807,\r\n"
            + "                \"UniversalId\": \"monaco_01\",\r\n"
            + "                \"Name\": \"Monaco 01\"\r\n"
            + "            },\r\n"
            + "            \"category\": \"Function\",\r\n"
            + "            \"classifications\": [\r\n"
            + "                \"Function\",\r\n"
            + "                \"Function\",\r\n"
            + "                \"Network\",\r\n"
            + "                \"Generic Node\",\r\n"
            + "                \"EMS Node\"\r\n"
            + "            ],\r\n"
            + "            \"classificationPath\": \"Function(\\\"Function/Network/Generic Node/EMS Node\\\")\",\r\n"
            + "            \"name\": \"Monaco 01\",\r\n"
            + "            \"universalId\": \"monaco_01\"\r\n"
            + "        }\r\n"
            + "    ],\r\n"
            + "    \"parents\": [],\r\n"
            + "    \"origins\": [],\r\n"
            + "    \"documentations\": [\r\n"
            + "        {\r\n"
            + "            \"category\": \"Documentation\",\r\n"
            + "            \"classifications\": [\r\n"
            + "                \"Documentation\",\r\n"
            + "                \"Comment\",\r\n"
            + "                \"Incident Comment\"\r\n"
            + "            ],\r\n"
            + "            \"classificationPath\": \"Documentation(\\\"Comment/Incident Comment\\\")\",\r\n"
            + "            \"name\": \"Life cycle action comment - Resolve\",\r\n"
            + "            \"universalId\": \"DOC-00003088\",\r\n"
            + "            \"date\": 1715179491844,\r\n"
            + "            \"comment\": \"Resolved Comment\\n-------\\nStatus changed from \\\"In Process\\\" to \\\"Resolved\\\"\",\r\n"
            + "            \"owner\": \"API OpenNMS\"\r\n"
            + "        },\r\n"
            + "        {\r\n"
            + "            \"category\": \"Documentation\",\r\n"
            + "            \"classifications\": [\r\n"
            + "                \"Documentation\",\r\n"
            + "                \"Comment\",\r\n"
            + "                \"Incident Comment\"\r\n"
            + "            ],\r\n"
            + "            \"classificationPath\": \"Documentation(\\\"Comment/Incident Comment\\\")\",\r\n"
            + "            \"name\": \"Life cycle action comment - Reactivate\",\r\n"
            + "            \"universalId\": \"DOC-00003087\",\r\n"
            + "            \"date\": 1715179480312,\r\n"
            + "            \"comment\": \"tes\\n-------\\nStatus changed from \\\"Resolved\\\" to \\\"In Process\\\"\",\r\n"
            + "            \"owner\": \"Admin\",\r\n"
            + "            \"submitter\": \"Admin\"\r\n"
            + "        },\r\n"
            + "        {\r\n"
            + "            \"category\": \"Documentation\",\r\n"
            + "            \"classifications\": [\r\n"
            + "                \"Documentation\",\r\n"
            + "                \"Comment\",\r\n"
            + "                \"Incident Comment\"\r\n"
            + "            ],\r\n"
            + "            \"classificationPath\": \"Documentation(\\\"Comment/Incident Comment\\\")\",\r\n"
            + "            \"name\": \"Life cycle action comment - Resolve\",\r\n"
            + "            \"universalId\": \"DOC-00003086\",\r\n"
            + "            \"date\": 1715178156607,\r\n"
            + "            \"comment\": \"Root Cause: Software [Provider]\\n-------\\ntest\\n-------\\nStatus changed from \\\"In Process\\\" to \\\"Resolved\\\"\",\r\n"
            + "            \"owner\": \"Admin\",\r\n"
            + "            \"submitter\": \"Admin\"\r\n"
            + "        },\r\n"
            + "        {\r\n"
            + "            \"category\": \"Documentation\",\r\n"
            + "            \"classifications\": [\r\n"
            + "                \"Documentation\",\r\n"
            + "                \"Comment\",\r\n"
            + "                \"Incident Comment\"\r\n"
            + "            ],\r\n"
            + "            \"classificationPath\": \"Documentation(\\\"Comment/Incident Comment\\\")\",\r\n"
            + "            \"name\": \"Life cycle action comment - Start\",\r\n"
            + "            \"universalId\": \"DOC-00003084\",\r\n"
            + "            \"date\": 1715178133705,\r\n"
            + "            \"comment\": \"test\\n-------\\nStatus changed from \\\"Open\\\" to \\\"In Process\\\"\",\r\n"
            + "            \"owner\": \"Admin\",\r\n"
            + "            \"submitter\": \"Admin\"\r\n"
            + "        }\r\n"
            + "    ]\r\n"
            + "}";
   
   @Test
   public void testEventExtendedObject() throws StreamWriteException, DatabindException, IOException {
      
      JsonMapper mapper = new JsonMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

      TroubleTicketEventExtended request2 = mapper.readValue(EXTENDED_EVENT_EXAMPLE1, TroubleTicketEventExtended.class);
      
      JsonNode customAttributes = request2.getCustomAttributes();
      System.out.println("custom attributes class "+ customAttributes.getClass().getName()
               + "\n value is\n"+customAttributes.toString());
      
      System.out.println("unmarshalled EventExtended is\n"+request2.toString());
      
      //assertTrue(request.toString().equals(request2.toString()));
      
   }
   
   @Test
   public void testPopulateEventExtended() throws StreamWriteException, DatabindException, IOException {
      System.out.println("START testPopulateEventExtended()");
      
      JsonMapper mapper = new JsonMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      
      TroubleTicketEventExtended request = new TroubleTicketEventExtended();
      
      request.setClassificationPath("Event(\"Event/Support/Incident/Monitoring Incident\")");
      request.setDescription("this is a dummy tiket - something happened");
      request.setLongDescription("something really really happened");
      request.setUniversalId("EVT-12345");

      request.setAlarmId("1234");
      
      StringWriter stringwriter = new StringWriter();
      mapper.writeValue(stringwriter, request);
      
      String reaquestStr = stringwriter.toString();
      System.out.println("TroubleTicketEventExtended JSON is\n"+reaquestStr);
     
      TroubleTicketEventExtended request2 = mapper.readValue(reaquestStr, TroubleTicketEventExtended.class);
      System.out.println("unmarshalled TroubleTicketEventExtended is\n"+request2.toString());
      
      assertTrue(request.toString().equals(request2.toString()));
      
   }
   
 
}
