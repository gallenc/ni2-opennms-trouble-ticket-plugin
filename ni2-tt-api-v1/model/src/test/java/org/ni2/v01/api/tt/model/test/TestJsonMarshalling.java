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
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class TestJsonMarshalling {
   

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
   
   @Test
   public void testLifecycleRequestObject() throws StreamWriteException, DatabindException, IOException {
      
      JsonMapper mapper = new JsonMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      
      LifecycleActionRequest request = new LifecycleActionRequest();
      
      boolean thrown = false;
      try {
          request.setAction("incorrect");
      } catch (IllegalArgumentException e) {
         System.out.println("expected error: "+e);
          thrown = true;
      }
      assertTrue(thrown);
      
      request.setAction(LifecycleActionRequest.CANCEL);
      request.setComment("cancelled for test");
      
      StringWriter stringwriter = new StringWriter();
      mapper.writeValue(stringwriter, request);
      
      String reaquestStr = stringwriter.toString();
      System.out.println("LifecycleActionRequest JSON is\n"+reaquestStr);
     
      LifecycleActionRequest request2 = mapper.readValue(reaquestStr, LifecycleActionRequest.class);
      System.out.println("unmarshalled LifecycleActionRequest is\n"+request2.toString());
      
      assertTrue(request.toString().equals(request2.toString()));
      
   }
   
   @Test
   public void testTroubleTicketCreateRequestObject() throws StreamWriteException, DatabindException, IOException {
      
      JsonMapper mapper = new JsonMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      
      TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
      
      request.setDescription("my descritpion");
      request.setLongDescription("my long description");
      request.setResourceIds(Arrays.asList("resource01","resource02"));
      
      request.setAlarmId("1234567");
      request.setAlarmSource("opennms");
      request.setTTCategory("Network");
      
      StringWriter stringwriter = new StringWriter();
      mapper.writeValue(stringwriter, request);
      
      String reaquestStr = stringwriter.toString();
      System.out.println("TroubleTicketCreateRequest JSON is\n"+reaquestStr);
     
      TroubleTicketCreateRequest request2 = mapper.readValue(reaquestStr, TroubleTicketCreateRequest.class);
      System.out.println("unmarshalled TroubleTicketCreateRequest is\n"+request2.toString());
      
      // note this works because toString does not use customAttributes directly
      assertTrue(request.toString().equals(request2.toString()));
      
   }
   
   @Test
   public void testTroubleTicketGetRequestObject() throws StreamWriteException, DatabindException, IOException {
      
      JsonMapper mapper = new JsonMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      
      TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
      
      request.setDescription("my descritpion");
      request.setLongDescription("my long description");
      request.setResourceIds(Arrays.asList("resource01","resource02"));
      
      request.setAlarmId("1234567");
      request.setAlarmSource("opennms");
      request.setTTCategory("Network");
      
      StringWriter stringwriter = new StringWriter();
      mapper.writeValue(stringwriter, request);
      
      String reaquestStr = stringwriter.toString();
      System.out.println("TroubleTicketCreateRequest JSON is\n"+reaquestStr);
     
      TroubleTicketCreateRequest request2 = mapper.readValue(reaquestStr, TroubleTicketCreateRequest.class);
      System.out.println("unmarshalled TroubleTicketCreateRequest is\n"+request2.toString());
      
      // note this works because toString does not use customAttributes directly
      assertTrue(request.toString().equals(request2.toString()));
      
   }

}
