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

import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;
import org.opennms.integration.api.v1.ticketing.Ticket;
import org.opennms.integration.api.v1.ticketing.Ticket.State;
import org.opennms.integration.api.v1.ticketing.TicketingPlugin;
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

   public String ttServerUrl = DEFAULT_TT_SERVER_URL;
   public String ttUsername = DEFAULT_TT_USERNAME;
   public String ttPassword = DEFAULT_TT_PASSWORD;
   public String trustAllCertificates ="true";

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

   @Before
   public void setup() {
      ttplugin = new Ni2TicketerPlugin();
      ttplugin.setTtPassword(ttPassword);
      ttplugin.setTtUsername(ttUsername);
      ttplugin.setTtServerUrl(ttServerUrl);
      ttplugin.setTtTrustAllCertificates(trustAllCertificates);
      
      ttplugin.setOnmsInstanceId(onmsInstance);
      ttplugin.setFallbackResourceId(fallbackResourceId);
      
      ttplugin.init();

   }

   @Test
   public void testTicket() {

      final Builder builder = ImmutableTicket.newBuilder();
      builder.setSummary("test ticket");
      builder.setDetails("test ticket details");
      builder.setState(State.OPEN);
      builder.setUser("opennms");
      builder.setAlarmId(1);
      Ticket newTicket = builder.build();

      String ticketId = ttplugin.saveOrUpdate(newTicket);
      LOG.debug("testTicket created ticketId: " + ticketId);
      assertNotNull(ticketId);
      
      Ticket createdTicket = ttplugin.get(ticketId);
      assertNotNull(createdTicket);
      LOG.debug("testTicket createdTicket: " + createdTicket);

   }

}
