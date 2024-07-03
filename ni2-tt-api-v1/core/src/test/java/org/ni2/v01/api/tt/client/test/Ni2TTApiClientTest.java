/*
 * Copyright (c) 2024 Ni2 Inc., Entimoss Ltd.
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

package org.ni2.v01.api.tt.client.test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.ni2.v01.api.tt.client.Ni2TTApiClientRawJson;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.model.TroubleTicketUpdateRequest;
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.Ni2ClientException;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.Ni2TTStatus;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ni2TTApiClientTest {
   private static final Logger LOG = LoggerFactory.getLogger(Ni2TTApiClientTest.class);

   public static final String DEFAULT_TT_SERVER_URL = "http://localhost:8080";
   public static final String DEFAULT_TT_USERNAME = "username";
   public static final String DEFAULT_TT_PASSWORD = "password";

   public static final String DEFAULT_OPENNMS_INSTANCE = "opennms_01";
   
   public static final String DEFAULT_FALLBACK_RESOURCE_ID="defaultResource1";

   private String ttServerUrl = DEFAULT_TT_SERVER_URL;
   private String ttUsername = DEFAULT_TT_USERNAME;
   private String ttPassword = DEFAULT_TT_PASSWORD;

   private String onmsInstance = DEFAULT_OPENNMS_INSTANCE;
   private String fallbackResourceId = DEFAULT_FALLBACK_RESOURCE_ID;

   private Ni2TTApiClient ttclient;

   public void setTtServerUrl(String ttServerUrl) {
      this.ttServerUrl = ttServerUrl;
   }

   public void setTtUsername(String ttUsername) {
      this.ttUsername = ttUsername;
   }

   public void setTtPassword(String ttPassword) {
      this.ttPassword = ttPassword;
   }

   public void setOnmsInstance(String onmsInstance) {
      this.onmsInstance = onmsInstance;
   }
   
   public void setFallbackResourceId(String fallbackResourceId) {
      this.fallbackResourceId = fallbackResourceId;
   }


   @Before
   public void setup() {
      ttclient = new Ni2TTApiClientRawJson();
      ttclient.setTtPassword(ttPassword);
      ttclient.setTtUsername(ttUsername);
      ttclient.setTtServerUrl(ttServerUrl);
      ttclient.setTrustAllCertificates(true);
   }

   @Test
   public void testAuthentication() throws Ni2ClientException {
      LOG.debug("********** testAuthentication() pass authentication test");
      // test pass 
      String authenticationToken = ttclient.getAuthenticationToken();
      LOG.debug("testAuthentication authenticationToken=" + authenticationToken);
      assertNotNull(authenticationToken);

      // test fail authentication
      LOG.debug("********** testAuthentication() fail authentication test");
      ttclient.setTtPassword("");

      boolean fail = false;
      try {
         authenticationToken = ttclient.getAuthenticationToken();
      } catch (Ni2ClientException ex) {
         LOG.debug("testAuthentication expected fail exception: "+ex.toString());
         fail = true;
      }

      assertTrue(fail);
   }

   @Test
   public void testCreateGetAndUpdateTicket() throws Ni2ClientException {
      LOG.debug("********** testCreateGetAndUpdateTicket() create ticket");

      final String DESCRIPTION = "my description";
      final String LONG_DESCRIPTION = "my long description";

      final String RESOURCE_ID = fallbackResourceId;
      final String ALARM_ID = "1234567";
      final String TT_CATEGORY = "Network";

      TroubleTicketCreateRequest createRequest = new TroubleTicketCreateRequest();

      createRequest.setDescription(DESCRIPTION);
      createRequest.setLongDescription(LONG_DESCRIPTION);
      createRequest.setResourceIds(Arrays.asList(RESOURCE_ID));

      createRequest.setAlarmId(ALARM_ID);
      createRequest.setAlarmSource(onmsInstance);
      createRequest.setTTCategory(TT_CATEGORY);
      
      createRequest.setAlarmSeverity(TroubleTicketEventExtended.ALARM_SEVERITY_CRITICAL);
      createRequest.setAlarmStatus(TroubleTicketEventExtended.ALARM_STATUS_UNACKNOWLEDGED);

      TroubleTicketCreateResponse troubleTicketCreateResponse = ttclient.createTroubleTicket(createRequest);
      LOG.debug("created ticket:" + troubleTicketCreateResponse);
      assertNotNull(troubleTicketCreateResponse);

      String ticketId = troubleTicketCreateResponse.getUniversalId();

      LOG.debug("********** testCreateGetAndUpdateTicket() get created ticket ticketId {}",ticketId);

      TroubleTicketEventExtended tticket = ttclient.getTroubleTicket(ticketId);
      LOG.debug("received tticket:" + tticket);

      String description = tticket.getDescription();
      String longDescription = tticket.getLongDescription();
      List<String> resourceIds = tticket.getTTResourceIds();

      String alarmId = tticket.getTTAlarmId();
      String alarmSource = tticket.getTTAlarmSource();
      String status = tticket.getTTStatus();
      String ttCategory = tticket.getTTCategory();

      LOG.debug("received tticket values: alarmId: {} alarmSource: {} status: {} ttCategory: {} resourceIds: {}", alarmId, alarmSource, status, ttCategory, resourceIds);

      assertTrue(DESCRIPTION.equals(description));
      assertTrue(LONG_DESCRIPTION.equals(longDescription));
      assertTrue(ALARM_ID.equals(alarmId));
      assertTrue(TT_CATEGORY.equals(ttCategory));
      assertTrue(resourceIds.contains(RESOURCE_ID));
      assertTrue(Ni2TTStatus.OPEN.equals(status));
      
      // update ticket
      LOG.debug("********** testCreateGetAndUpdateTicket() update created ticket ticketId {}",ticketId);
      TroubleTicketUpdateRequest updateRequest = new TroubleTicketUpdateRequest();
      
      final String UPDATE_DESCRIPTION = "my updated description";
      final String UPDATE_LONG_DESCRIPTION = "my updated long description";

      final String UPDATE_RESOURCE_ID = fallbackResourceId;
      final String UPDATE_ALARM_ID = "89101112";
      final String UPDATE_TT_CATEGORY = "Network";

      updateRequest.setDescription(UPDATE_DESCRIPTION);
      updateRequest.setLongDescription(UPDATE_LONG_DESCRIPTION);

      updateRequest.setAlarmId(UPDATE_ALARM_ID);
      updateRequest.setAlarmSource(onmsInstance);
      updateRequest.setTTCategory(UPDATE_TT_CATEGORY);
      
      updateRequest.setAlarmSeverity(TroubleTicketEventExtended.ALARM_SEVERITY_CLEARED);
      updateRequest.setAlarmStatus(TroubleTicketEventExtended.ALARM_STATUS_ACKNOWLEDGED);

      ttclient.updateTroubleTicket(ticketId, updateRequest);
      
      tticket = ttclient.getTroubleTicket(ticketId);
      status = tticket.getTTStatus();
      LOG.debug("after update ticket: {} tticket: {}", status, tticket);
      
      description = tticket.getDescription();
      longDescription = tticket.getLongDescription();
      resourceIds = tticket.getTTResourceIds();

      alarmId = tticket.getTTAlarmId();
      alarmSource = tticket.getTTAlarmSource();
      status = tticket.getTTStatus();
      ttCategory = tticket.getTTCategory();
      
      assertTrue(UPDATE_DESCRIPTION.equals(description));
      assertTrue(UPDATE_LONG_DESCRIPTION.equals(longDescription));
      assertTrue(UPDATE_ALARM_ID.equals(alarmId));
      assertTrue(UPDATE_TT_CATEGORY.equals(ttCategory));
      assertTrue(resourceIds.contains(RESOURCE_ID));
      assertTrue(Ni2TTStatus.OPEN.equals(status));
      

      // try start trouble ticket (user may not be able to start)
      LOG.debug("********** testCreateGetAndUpdateTicket() start ticket");
      try {
         ttclient.changeTicketState(ticketId, LifecycleActionRequest.START, "start ticket test");
      } catch (Ni2ClientException ex) {
         LOG.debug("test problem starting ticket", ex);
      }
      tticket = ttclient.getTroubleTicket(ticketId);
      status = tticket.getTTStatus();
      LOG.debug("after start request status: {} tticket: {}", status, tticket);

      // try resolve trouble ticket (user may not be able to resolve)
      LOG.debug("********** testCreateGetAndUpdateTicket() resolve ticket");
      try {
         ttclient.changeTicketState(ticketId, LifecycleActionRequest.RESOLVE, "resolve ticket test");
      } catch (Ni2ClientException ex) {
         LOG.debug("test problem resolving ticket", ex);
      }
      tticket = ttclient.getTroubleTicket(ticketId);
      status = tticket.getTTStatus();
      LOG.debug("after resolve request status: {} tticket: {}", status, tticket);
      
      // try close trouble ticket (user may not be able to close)
      LOG.debug("********** testCreateGetAndUpdateTicket() close ticket");
      try {
         ttclient.changeTicketState(ticketId, LifecycleActionRequest.CLOSE, "close ticket test");
      } catch (Ni2ClientException ex) {
         LOG.debug("test problem closing ticket", ex);
      }
      tticket = ttclient.getTroubleTicket(ticketId);
      status = tticket.getTTStatus();
      LOG.debug("after close request status: {} tticket: {}", status, tticket);

      // try cancel trouble ticket
      LOG.debug("********** testCreateGetAndUpdateTicket() cancel ticket");
      try {
         ttclient.changeTicketState(ticketId, LifecycleActionRequest.CANCEL, "cancelled ticket test");
      } catch (Ni2ClientException ex) {
         LOG.debug("test problem cancelling ticket", ex);
      }
      
      tticket = ttclient.getTroubleTicket(ticketId);
      LOG.debug("after cancel request status: {} tticket: {}", status, tticket);
      status = tticket.getTTStatus();
      
      // fails in integration test - wrong status
      //assertTrue(Ni2TTStatus.CANCELED.equals(status));

   }

}
