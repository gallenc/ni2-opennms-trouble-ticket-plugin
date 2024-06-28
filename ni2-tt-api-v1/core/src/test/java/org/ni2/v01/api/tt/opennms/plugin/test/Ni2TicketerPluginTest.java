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

package org.ni2.v01.api.tt.opennms.plugin.test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;
import org.opennms.integration.api.v1.ticketing.Ticket;
import org.opennms.integration.api.v1.ticketing.Ticket.State;
import org.opennms.integration.api.v1.ticketing.immutables.ImmutableTicket;
import org.opennms.integration.api.v1.ticketing.immutables.ImmutableTicket.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ni2TicketerPluginTest {
   private static final Logger LOG = LoggerFactory.getLogger(Ni2TicketerPluginTest.class);
   
   public static final String DEFAULT_TT_SERVER_URL = "http://localhost:8080";
   public static final String DEFAULT_TT_USERNAME = "username";
   public static final String DEFAULT_TT_PASSWORD = "password";
   
   public static final String DEFAULT_OPENNMS_INSTANCE = "opennms_01";
   public static final String DEFAULT_FALLBACK_RESOURCE_ID="defaultResource1";
   public static final String DEFAULT_CONNECTION_TIMEOUT="5000"; //5 s
   

   public String ttServerUrl = DEFAULT_TT_SERVER_URL;
   public String ttUsername = DEFAULT_TT_USERNAME;
   public String ttPassword = DEFAULT_TT_PASSWORD;
   public String trustAllCertificates ="true";
   public String connectionTimeoutStr = DEFAULT_CONNECTION_TIMEOUT;

   private String onmsInstance = DEFAULT_OPENNMS_INSTANCE;
   private String fallbackResourceId = DEFAULT_FALLBACK_RESOURCE_ID;
   
   private Ni2TicketerPlugin ttplugin;

   public void setTtServerUrl(String ttServerUrl) {
      this.ttServerUrl = ttServerUrl;
   }

   public void setTtUsername(String ttUsername) {
      this.ttUsername = ttUsername;
   }

   public void setTtPassword(String ttPassword) {
      this.ttPassword = ttPassword;
   }

   public void setTrustAllCertificates(String trustAllCertificates) {
      this.trustAllCertificates = trustAllCertificates;
   }

   public void setOnmsInstance(String onmsInstance) {
      this.onmsInstance = onmsInstance;
   }
   
   public void setFallbackResourceId(String fallbackResourceId) {
      this.fallbackResourceId = fallbackResourceId;
   }
   
   public void setConnectionTimeoutStr(String connectionTimeoutStr) {
      this.connectionTimeoutStr = connectionTimeoutStr;
   }

   @Before
   public void setup() {
      ttplugin = new Ni2TicketerPlugin();
      ttplugin.setTtPassword(ttPassword);
      ttplugin.setTtUsername(ttUsername);
      ttplugin.setTtServerUrl(ttServerUrl);
      ttplugin.setTtTrustAllCertificates(trustAllCertificates);
      
      ttplugin.setOnmsInstanceId(onmsInstance);
      ttplugin.setFallbackResourceId(fallbackResourceId);

      ttplugin.setConnectionTimeout(connectionTimeoutStr);
      
      ttplugin.init();

   }

   @Test
   public void testTicket() {
      LOG.debug("**** testTicket create new ticket");
      
      final String DESCRIPTION = "testTicket my description";
      final String LONG_DESCRIPTION = "testTicket my long description";

      final String RESOURCE_ID = fallbackResourceId;
      final Integer ALARM_ID = 1234567;
      final State TICKET_STATE = State.OPEN;
      final String TICKET_USER = "opennms";
      final String TICKET_CATEGORY="Network";

      Builder builder = ImmutableTicket.newBuilder();
      builder.setSummary(DESCRIPTION);
      builder.setDetails(LONG_DESCRIPTION);
      builder.setState(TICKET_STATE);
      builder.setUser(TICKET_USER);
      builder.setAlarmId(ALARM_ID);
      
      // add alarm details to attributes map
      Map<String, String> attributesMap = new LinkedHashMap<>();
      attributesMap.put(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_RESOURCE_IDS, RESOURCE_ID);
      attributesMap.put(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_STATUS, TroubleTicketEventExtended.ALARM_STATUS_ACKNOWLEDGED);
      attributesMap.put(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_SEVERITY, TroubleTicketEventExtended.ALARM_SEVERITY_CRITICAL);
      attributesMap.put(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_CATEGORY, TICKET_CATEGORY);
      
      
      builder.setAttributes(attributesMap);
      Ticket newTicket = builder.build();

      // now create new ticket
      String ticketId = ttplugin.saveOrUpdate(newTicket);
      LOG.debug("**** testTicket created ticketId: " + ticketId);
      assertNotNull(ticketId);
      
      Ticket createdTicket = ttplugin.get(ticketId);
      assertNotNull(createdTicket);
      LOG.debug("**** testTicket createdTicket: " + createdTicket);

      assertEquals(DESCRIPTION,createdTicket.getSummary());
      assertEquals(LONG_DESCRIPTION,createdTicket.getDetails());
      assertEquals(TICKET_STATE,createdTicket.getState());
      assertEquals(ALARM_ID,createdTicket.getAlarmId());
      
      // TODO user is not pushed to ni2 ticket so can't test
      // assertEquals(TICKET_USER,createdTicket.getUser());
      
      Map<String, String> createdTicketAttributesMap = createdTicket.getAttributes();
      assertNotNull(createdTicketAttributesMap);
      
      assertEquals(TroubleTicketEventExtended.ALARM_STATUS_ACKNOWLEDGED, attributesMap.get(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_STATUS));
      assertEquals(TroubleTicketEventExtended.ALARM_SEVERITY_CRITICAL, attributesMap.get(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_SEVERITY));
      
      assertEquals(TICKET_CATEGORY, attributesMap.get(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_CATEGORY));

      LOG.debug("**** testTicket updating created ticket");
      
      final String UPDATE_DESCRIPTION = "testTicket my updated description";
      final String UPDATE_LONG_DESCRIPTION = "testTicket my updated long description";

      
      builder = ImmutableTicket.newBuilderFrom(createdTicket);
      builder.setSummary(UPDATE_DESCRIPTION);
      builder.setDetails(UPDATE_LONG_DESCRIPTION);

      
      // add alarm details to attributes map
      Map<String,String> attributesMap2 = new LinkedHashMap<>();
      if (createdTicket.getAttributes()!=null) {
         attributesMap2.putAll(createdTicket.getAttributes());
      }
      
      attributesMap2.put(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_STATUS, TroubleTicketEventExtended.ALARM_STATUS_UNACKNOWLEDGED);
      attributesMap2.put(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_SEVERITY, TroubleTicketEventExtended.ALARM_SEVERITY_CLEARED);
      
      builder.setAttributes(attributesMap2);
      
      Ticket updateTicket = builder.build();
      
      LOG.debug("**** testTicket updated ticket to send {}",updateTicket );
      
      ttplugin.saveOrUpdate(updateTicket);
      
      Ticket updatedTicket = ttplugin.get(ticketId);
      assertNotNull(updatedTicket);
      LOG.debug("**** testTicket updatedTicket: " + updatedTicket);
      
      assertEquals(UPDATE_DESCRIPTION,updatedTicket.getSummary());
      assertEquals(UPDATE_LONG_DESCRIPTION,updatedTicket.getDetails());
      assertEquals(ALARM_ID,updatedTicket.getAlarmId());
      
      Map<String, String> updatedTicketAttributesMap = updatedTicket.getAttributes();
      assertNotNull(updatedTicketAttributesMap);
      
      assertEquals(TroubleTicketEventExtended.ALARM_STATUS_UNACKNOWLEDGED, updatedTicketAttributesMap.get(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_STATUS));
      assertEquals(TroubleTicketEventExtended.ALARM_SEVERITY_CLEARED, updatedTicketAttributesMap.get(Ni2TicketerPlugin.ONMS_TICKET_ATTRIBUTE_KEY_ALARM_SEVERITY));

   }

}
