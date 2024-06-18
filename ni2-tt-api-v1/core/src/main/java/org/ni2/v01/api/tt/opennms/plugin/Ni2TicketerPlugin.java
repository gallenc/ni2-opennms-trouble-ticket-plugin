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

package org.ni2.v01.api.tt.opennms.plugin;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ni2.v01.api.tt.client.Ni2TTApiClientRawJson;
import org.ni2.v01.api.tt.model.Ni2ClientException;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.Ni2TTStatus;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.opennms.integration.api.v1.ticketing.Ticket;
import org.opennms.integration.api.v1.ticketing.Ticket.State;
import org.opennms.integration.api.v1.ticketing.TicketingPlugin;
import org.opennms.integration.api.v1.ticketing.immutables.ImmutableTicket;
import org.opennms.integration.api.v1.ticketing.immutables.ImmutableTicket.Builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ni2TicketerPlugin implements TicketingPlugin {
   private static final Logger LOG = LoggerFactory.getLogger(Ni2TicketerPlugin.class);
   
   // maximum length of a description in ni2 ticket
   public static final int MAX_DESCRIPTION_LENGTH = 255;

   public static final String TT_SERVER_URL_PROPERTY = "ni2.tt.server.url";
   public static final String TT_USERNAME_PROPERTY = "ni2.tt.server.username";
   public static final String TT_PASSWORD_PROPERTY = "ni2.tt.server.password";
   public static final String TT_TRUST_ALL_CERTIFICATES_PROPERTY = "ni2.tt.server.trustallcertificates";
   public static final String TT_OPENNMS_INSTANCE_PROPERTY = "ni2.tt.opennms.instance";
   public static final String TT_FALLBACK_RESOURCE_PROPERTY = "ni2.tt.opennms.fallbackresource";

   public static final String DEFAULT_TT_SERVER_URL_PROPERTY = "http://localhost:8080";
   public static final String DEFAULT_TT_USERNAME_PROPERTY = "username";
   public static final String DEFAULT_TT_PASSWORD_PROPERTY = "password";
   public static final String DEFAULT_TT_OPENNMS_INSTANCE_PROPERTY = "OpenNMS-Instance-not-set";
   public static final String DEFAULT_TT_TRUST_ALL_CERTIFICATES_PROPERTY = "true";
   public static final String DEFAULT_TT_FALLBACK_RESOURCE_ID = "DEFAULT_RESOURCE_ID";

   // trouble ticket contains csv list of resourceids (node names)
   public static final String ONMS_TICKET_ATTRIBUTE_KEY_RESOURCE_IDS = "ni2.tt.resourceids";
   
   // key to access user defined atributes
   public static final String ONMS_TICKET_NI2_ATTRIBUTES_KEY_PREFIX = "ni2.tt.attributes.";

   private String ttServerUrl = DEFAULT_TT_SERVER_URL_PROPERTY;
   private String ttUsername = DEFAULT_TT_USERNAME_PROPERTY;
   private String ttPassword = DEFAULT_TT_PASSWORD_PROPERTY;
   private boolean ttTrustAllCertificates = Boolean.valueOf(DEFAULT_TT_TRUST_ALL_CERTIFICATES_PROPERTY);
   private String onmsInstanceId = DEFAULT_TT_OPENNMS_INSTANCE_PROPERTY;
   private String fallbackResourceId = DEFAULT_TT_FALLBACK_RESOURCE_ID;

   Ni2TTApiClient ttclient = null;

   public void setTtServerUrl(String ttServerUrl) {
      this.ttServerUrl = ttServerUrl;
   }

   public void setTtUsername(String ttUsername) {
      this.ttUsername = ttUsername;
   }

   public void setTtPassword(String ttPassword) {
      this.ttPassword = ttPassword;
   }

   public void setTtTrustAllCertificates(String ttTrustAllCertificates) {
      this.ttTrustAllCertificates = Boolean.valueOf(ttTrustAllCertificates);
   }

   public void setOnmsInstanceId(String onmsInstanceId) {
      this.onmsInstanceId = onmsInstanceId;
   }

   public void setFallbackResourceId(String fallbackResourceId) {
      this.fallbackResourceId = fallbackResourceId;
   }

   public void init() {
      LOG.info("Ni2 Trouble Ticket Plugin initialised ttServerUrl={}  ttUsername={} ttPassword=not shown ttTrustAllCertificates={} onmsInstanceId={} fallbackResourceId={}", ttServerUrl, ttUsername, ttTrustAllCertificates, onmsInstanceId, fallbackResourceId);

      ttclient = new Ni2TTApiClientRawJson();
      ttclient.setTtServerUrl(ttServerUrl);
      ttclient.setTtUsername(ttUsername);
      ttclient.setTtPassword(ttPassword);
      ttclient.setTrustAllCertificates(ttTrustAllCertificates);

   }

   @Override
   public Ticket get(String ticketId) {
      LOG.debug("get ticketId {}", ticketId);
      if (ttclient == null)
         throw new IllegalStateException("ttclient=null. init() must be called on Ni2Ticketer plugin before use.");

      if (ticketId == null) {
         LOG.error("No Ni2 ticketID available in OpenNMS Ticket");
         throw new Ni2TicketerException("No ni2 ticketID available in OpenNMS Ticket");
      }

      try {
         TroubleTicketEventExtended troubleTicketEventExtended = ttclient.getTroubleTicket(ticketId);
         LOG.debug("received ni2 ticket ", troubleTicketEventExtended);

         final Builder builder = ImmutableTicket.newBuilder();
         builder.setId(ticketId);
         builder.setSummary(troubleTicketEventExtended.getDescription());
         builder.setDetails(troubleTicketEventExtended.getLongDescription());
         builder.setState(ni2StateToONMSState(troubleTicketEventExtended.getStatus()));
         builder.setUser("troubleTicketUser");
         Ticket ticket = builder.build();

         LOG.debug("mapped ni2 ticket to onms ticket : {}", ticket);
         return ticket;

      } catch (Ni2ClientException ex) {
         LOG.error("unable to get ticketId {}", ticketId, ex);
         throw new Ni2TicketerException("unable to get ticketId " + ticketId, ex);
      }

   }

   @Override
   public String saveOrUpdate(Ticket ticket) {
      LOG.debug("saveOrUpdate ticket {}", ticket);
      if (ttclient == null)
         throw new IllegalStateException("ttclient=null. init() must be called on Ni2Ticketer plugin before use.");

      if ((ticket.getId() == null)) {
         return save(ticket);
      } else {
         update(ticket);
      }
      return ticket.getId();

   }

   private String save(Ticket ticket) {
      LOG.debug("save ticket {}", ticket);

      TroubleTicketCreateRequest createRequest = new TroubleTicketCreateRequest();
      createRequest.setAlarmId(ticket.getAlarmId().toString());
      createRequest.setAlarmSource(onmsInstanceId);
      
      // summary must be limited in length
      String summary = ticket.getSummary().substring(0, Math.min(MAX_DESCRIPTION_LENGTH, ticket.getSummary().length()));
      createRequest.setDescription(summary);
      createRequest.setLongDescription(ticket.getDetails());

      // set default resource id
      createRequest.setResourceIds(Arrays.asList(fallbackResourceId));

      if (ticket.getAttributes() != null) {
         Map<String, String> ticketAttributeMap = ticket.getAttributes();
         
         String resources = ticketAttributeMap.get(ONMS_TICKET_ATTRIBUTE_KEY_RESOURCE_IDS);
         if (resources == null || resources.isBlank() || resources.contains(" ")) {
            LOG.debug("resources must not be null or empty or contain spaces. Using defaultResourceId=" + fallbackResourceId);
         } else {
            List<String> resourceIds = Arrays.asList(resources.split(","));
            createRequest.setResourceIds(resourceIds);
         }
         
         // any attribute in the OpenNMS Ticket with key starting with ONMS_TICKET_NI2_ATTRIBUTES_KEY_PREFIX will 
         // be added to the ni2 ticket attributes with the key suffix as the attributes key
         Map<String, String> additionalNi2AttributeMap = new LinkedHashMap<String, String>();
         for(String ticketAttributeKey : ticketAttributeMap.keySet()) {
            if(ticketAttributeKey.startsWith(ONMS_TICKET_NI2_ATTRIBUTES_KEY_PREFIX)) {
               String additionalAttributeKey = ticketAttributeKey.replace(ONMS_TICKET_NI2_ATTRIBUTES_KEY_PREFIX,"");
               String ticketAttributeValue = ticketAttributeMap.get(ticketAttributeKey);
               additionalNi2AttributeMap.put(additionalAttributeKey, ticketAttributeValue);
            }
         }
         
         createRequest.getCustomAttributes().putAll(additionalNi2AttributeMap);
         
      }

      TroubleTicketCreateResponse troubleTicketCreateResponse;
      try {
         troubleTicketCreateResponse = ttclient.createTroubleTicket(createRequest);

         return troubleTicketCreateResponse.getUniversalId();
      } catch (Ni2ClientException ex) {
         LOG.error("unable to create ticket {}", ticket, ex);
         throw new Ni2TicketerException("unable to create ticket " + ticket, ex);
      }

   }

   private void update(Ticket ticket) {
      LOG.debug("update ticket {}", ticket);
      //TODO CREATE UPDATE TICKET PROCESS

   }

   private State ni2StateToONMSState(String ni2Status) {

      switch (ni2Status) {
      case Ni2TTStatus.OPEN:
         return State.OPEN;
      case Ni2TTStatus.CLOSED:
         return State.CLOSED;
      case Ni2TTStatus.CANCELED:
         return State.CANCELLED;
      case Ni2TTStatus.RESOLVED:
         return State.CLOSED;
      default:
         return State.OPEN;
      }

   }


}
