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

package org.ni2.v01.api.tt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.ni2.v01.api.tt.model.AuthenticationRequest;
import org.ni2.v01.api.tt.model.AuthenticationResponse;
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.Ni2ClientException;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerException;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;
import org.ni2.v01.api.utils.TLSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import jdk.internal.org.jline.utils.Log;

public class Ni2TTApiClientCXF implements Ni2TTApiClient {

   private static final Logger LOG = LoggerFactory.getLogger(Ni2TicketerPlugin.class);

   private String ttServerUrl = null;
   private String ttUsername = null;
   private String ttPassword = null;
   private boolean trustAllCertificates = false;

   @Override
   public void setTtServerUrl(String ttServerUrl) {
      this.ttServerUrl = ttServerUrl;
   }

   @Override
   public void setTtUsername(String ttUsername) {
      this.ttUsername = ttUsername;
   }

   @Override
   public void setTtPassword(String ttPassword) {
      this.ttPassword = ttPassword;
   }

   @Override
   public void setTrustAllCertificates(boolean trustAllCertificates) {
      this.trustAllCertificates = trustAllCertificates;

   }

   /**
    * creates new web client with trust any TLS
    * @return
    */

   public WebClient getWebClient() {
      if (ttServerUrl == null || ttServerUrl.isBlank())
         LOG.error("ttServerUrl must not be null or empty");
      if (ttUsername == null || ttUsername.isBlank())
         LOG.error("ttUsername must not be null or empty");
      if (ttPassword == null)
         LOG.error("ttPassword must not be null");

      List<Object> providers = new ArrayList<>();
      providers.add(new JacksonJsonProvider());

      List<? extends Feature> features = Arrays.<Feature>asList(new LoggingFeature());

      WebClient webClient = WebClient.create(ttServerUrl, providers, features, null);

      //create(ttServerUrl, providers, );

      // Trust any TLS certificate
      if (trustAllCertificates) {
         ClientConfiguration configuration = webClient.getConfiguration();
         TLSUtils.addX509TrustManager(configuration);
      }

      webClient.header("Content-Type", MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

      return webClient;

   }

   /**
    * post {{baseUrl}}/api/v1/auth/login
    * payload: 
    *   {
    *     "username": "{{username}}",
    *     "password": "{{password}}"
    *   }
    * 
    * 
    * 
    * @return
    */
   @Override
   public String getAuthenticationToken() throws Ni2ClientException {

      WebClient webClient = getWebClient();

      try {

         AuthenticationRequest authenticationRequest = new AuthenticationRequest();
         authenticationRequest.setPassword(ttPassword);
         authenticationRequest.setUsername(ttUsername);

         Response response = webClient.path("/api/v1/auth/login").post(authenticationRequest);

         if (response.getStatus() == Response.Status.OK.getStatusCode()) {

            AuthenticationResponse authResponse = response.readEntity(AuthenticationResponse.class);
            return authResponse.getAccessToken();

         } else {
            String error = response.readEntity(String.class);
            throw new Ni2ClientException("failed Authentication login " + webClient.getBaseURI()
                     + " for username=" + ttUsername
                     + " error response:" + error);
         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem authenticating with " + webClient.getBaseURI(), ex);
      }

   }

   /**
    * post {{baseUrl}}/api/v1/entity/event/create/event
    * {
    *    "classificationPath": "Event(\"Event/Support/Incident/Monitoring Incident\")",
    *    "description": "{{description}}",
    *    "longDescription": "{{longDescription}}",
    *    "customAttributes": {
    *       "Category": "{{categoryUpdated}}",
    *       "AlarmSource": "{{alarmSource}}",
    *       "AlarmId": "{{alarmId}}"
    *    },
    *    "resourceIds": ["{{resourceUID}}"]
    * }
    * @throws Ni2ClientException 
    */
   @Override
   public TroubleTicketCreateResponse createTroubleTicket(TroubleTicketCreateRequest createRequest) throws Ni2ClientException {
      LOG.debug("createTroubleTicket:" + createRequest);

      String authenticationToken = getAuthenticationToken();

      WebClient webClient = getWebClient();

      try {

         Response response = webClient.header("Authorization", "Bearer " + authenticationToken)
                  .path("/api/v1/entity/event/create/event")
                  .post(createRequest);

         if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(TroubleTicketCreateResponse.class);
         } else {
            String error = response.readEntity(String.class);
            throw new Ni2ClientException("failed Trouble Ticket Create " + webClient.getBaseURI()
                     + " error response:" + error);
         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem creating trouble ticket", ex);
      }

   }

   /**
    * post {{baseUrl}}/api/v1/entity/event/get/event/extended/{{eventId}}
    * @param ticketId
    * @return
    * 
    * {
    *     "description": "something happened",
    *     "longDescription": "something happened a lot",
    *     "customAttributes": {
    *         "Description": "something happened",
    *         "CorrectedPendingTime": 0,
    *         "Category": "Network",
    *         "ResponseAgreementStatus": "In Compliance",
    *         "Escalation": false,
    *         "Expedite": false,
     *        "ActualStartDate": 1715178133658,
    *         "Knowledge": false,
    *         "Name": "EVT-00012395",
    *         "CorrectedResponseTime": 0,
    *         "Identifier": "EVT-00012395",
    *         "Submitter": "APIOpenNMS",
    *         "UnSqueezable": false,
    *         "RootCauseComment": "test",
    *         "ResponseTime": 0,
    *         "UniversalId": "EVT-00012395",
    *         "ResolutionDate": 1715179491750,
    *         "Status": "Resolved",
    *         "AddedStartPendingTime": 0,
     *        "PendingTime": 0,
    *         "LoanEquipment": "0 Loaned",
    *         "RequestSysMode": "new",
    *         "Priority": "3-Moderate",
    *         "RequiresAttention": false,
    *         "CorrectedResolutionTime": 1,
    *         "ResolutionAgreementStatus": "In Compliance",
    *         "ResolutionTime": 1,
    *         "LongDescription": "something happened a lot",
    *         "RootCause": "Software [Provider]",
    *         "LastModificationDate": 1715179491887,
    *         "AddedPendingTime": 0
    *     },
    *     "category": "Event",
    *     "classifications": [
    *         "Event",
    *         "Event",
    *         "Support",
    *         "Incident",
    *         "Monitoring Incident"
     *    ],
    *     "classificationPath": "Event(\"Event/Support/Incident/Monitoring Incident\")",
    *     "name": "EVT-00012395",
    *     "universalId": "EVT-00012395",
    *     "services": [],
    *     "resources": [
    *         {
    *             "customAttributes": {
    *                 "Status": "In Process",
    *                 "Identifier": "FUN-00002497",
    *                 "NamingConvention": "oneTimeFree",
    *                 "LastModificationDate": 1716936428807,
    *                 "UniversalId": "monaco_01",
    *                 "Name": "Monaco 01"
    *             },
    *             "category": "Function",
    *             "classifications": [
    *                 "Function",
    *                 "Function",
    *                 "Network",
    *                 "Generic Node",
    *                 "EMS Node"
    *             ],
    *             "classificationPath": "Function(\"Function/Network/Generic Node/EMS Node\")",
    *             "name": "Monaco 01",
    *             "universalId": "monaco_01"
    *         }
    *     ],
    *     "parents": [],
    *     "origins": [],
    *     "documentations": [
    *         {
    *             "category": "Documentation",
    *             "classifications": [
    *                 "Documentation",
    *                 "Comment",
    *                 "Incident Comment"
    *             ],
    *             "classificationPath": "Documentation(\"Comment/Incident Comment\")",
    *             "name": "Life cycle action comment - Resolve",
    *             "universalId": "DOC-00003088",
    *             "date": 1715179491844,
    *             "comment": "Resolved Comment\n-------\nStatus changed from \"In Process\" to \"Resolved\"",
    *             "owner": "API OpenNMS"
    *         },
    *         {
    *             "category": "Documentation",
    *             "classifications": [
    *                 "Documentation",
    *                 "Comment",
    *                 "Incident Comment"
    *             ],
    *             "classificationPath": "Documentation(\"Comment/Incident Comment\")",
    *             "name": "Life cycle action comment - Reactivate",
    *             "universalId": "DOC-00003087",
    *             "date": 1715179480312,
    *             "comment": "tes\n-------\nStatus changed from \"Resolved\" to \"In Process\"",
    *             "owner": "Admin",
    *             "submitter": "Admin"
    *         },
    *         {
    *             "category": "Documentation",
    *             "classifications": [
    *                 "Documentation",
    *                 "Comment",
    *                 "Incident Comment"
    *             ],
    *             "classificationPath": "Documentation(\"Comment/Incident Comment\")",
    *             "name": "Life cycle action comment - Resolve",
    *             "universalId": "DOC-00003086",
    *             "date": 1715178156607,
    *             "comment": "Root Cause: Software [Provider]\n-------\ntest\n-------\nStatus changed from \"In Process\" to \"Resolved\"",
    *             "owner": "Admin",
    *             "submitter": "Admin"
    *         },
    *         {
    *             "category": "Documentation",
    *             "classifications": [
    *                 "Documentation",
    *                 "Comment",
    *                 "Incident Comment"
    *             ],
    *             "classificationPath": "Documentation(\"Comment/Incident Comment\")",
    *             "name": "Life cycle action comment - Start",
    *             "universalId": "DOC-00003084",
    *             "date": 1715178133705,
    *             "comment": "test\n-------\nStatus changed from \"Open\" to \"In Process\"",
    *             "owner": "Admin",
    *             "submitter": "Admin"
    *         }
    *     ]
    * }
    * @throws Ni2ClientException 
    */
   @Override
   public TroubleTicketEventExtended getTroubleTicket(String ticketId) throws Ni2ClientException {

      LOG.debug("getTroubleTicket ticketId:" + ticketId);

      String authenticationToken = getAuthenticationToken();

      WebClient webClient = getWebClient();

      try {

         Response response = webClient.header("Authorization", "Bearer " + authenticationToken)
                  .path("/api/v1/entity/event/get/event/extended").path(ticketId)
                  .post(null);

         if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(TroubleTicketEventExtended.class);
         } else {
            String error = response.readEntity(String.class);
            throw new Ni2ClientException("failed Get Trouble Ticket " + webClient.getBaseURI()
                     + " error response:" + error);
         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem getting ticketId: " + ticketId, ex);
      }

   }

   //   /**
   //    * post {{baseUrl}}/api/v1/entity/event/executeLifecycleAction/event/{{eventId}}
   //    * payload:
   //    * {
   //    *  "action": "Close",
   //    *  "comment": "{{closeComment}}"
   //    *  }
   //    * 
   //    * @param ticketId
   //    */
   //   public void closeTroubleTicket(String ticketId, String comment) {
   //
   //   }
   //
   //   /**
   //    * post {{baseUrl}}/api/v1/entity/event/executeLifecycleAction/event/{{eventId}}
   //    * payload:
   //    * {
   //    *  "action": "Cancel",
   //    *  "comment": "{{closeComment}}"
   //    *  }
   //    * 
   //    * @param ticketId
   //    */
   //   public void cancelTroubleTicket(String ticketId, String comment) {
   //      LOG.debug("cancelTroubleTicket ticketId:"+ticketId);
   //      
   //      LifecycleActionRequest lifecycleActionRequest = new LifecycleActionRequest();
   //      lifecycleActionRequest.setAction(LifecycleActionRequest.CANCEL);
   //      lifecycleActionRequest.setComment(comment);
   //
   //      String authenticationToken = getAuthenticationToken();
   //
   //      WebClient webClient = getWebClient();
   //
   //      Response response = webClient.header("Authorization", "Bearer " + authenticationToken)
   //               .path("/api/v1/entity/event/executeLifecycleAction/event/").path(ticketId)
   //               .post(lifecycleActionRequest);
   //
   //      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
   //         LOG.debug("success cancelTroubleTicket ticketId:"+ticketId );
   //      } else {
   //         String error = response.readEntity(String.class);
   //         LOG.error("failed Get Trouble Ticket " + webClient.getBaseURI()
   //                  + " error response:" + error);
   //         
   //      }
   //
   //   }

   /**
    * post {{baseUrl}}/api/v1/entity/event/executeLifecycleAction/event/{{eventId}}
    * payload:
    * {
    *  "action": "Cancel",
    *  "comment": "{{closeComment}}"
    *  }
    *  action can be any of Close" "Cancel" "Resolve" (or others) determined by 
    *  states in LifecycleActionRequest.ALLOWED_ACTIONS_LIST
    *  LifecycleActionRequest.CLOSE, LifecycleActionRequest.CANCEL, LifecycleActionRequest.RESOLVE
    *  
    * @param ticketId
    * @param action
    * @param comment
    * @throws Ni2ClientException 
    */
   @Override
   public void changeTicketState(String ticketId, String action, String comment) throws Ni2ClientException {
      LOG.debug("calling changeTicketState ticketId: {} action {} comment: {} ", ticketId, action, comment);

      if (comment == null)
         throw new IllegalArgumentException("comment must be set");

      if (!LifecycleActionRequest.ALLOWED_ACTIONS_LIST.contains(action)) {
         throw new IllegalArgumentException("action " + action + "must be one of " + LifecycleActionRequest.ALLOWED_ACTIONS_LIST);
      }

      String authenticationToken = getAuthenticationToken();

      WebClient webClient = getWebClient();

      try {

         LifecycleActionRequest lifecycleActionRequest = new LifecycleActionRequest();
         lifecycleActionRequest.setAction(action);
         lifecycleActionRequest.setComment(comment);

         Response response = webClient.header("Authorization", "Bearer " + authenticationToken)
                  .path("/api/v1/entity/event/executeLifecycleAction/event/").path(ticketId)
                  .post(lifecycleActionRequest);

         if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            LOG.debug("success changeTicketState ticketId: {} action {} comment: {} ", ticketId, action, comment);
         } else {
            String error = response.readEntity(String.class);
            throw new Ni2ClientException("failed change ticket state " + webClient.getBaseURI()
                     + " error response:" + error);

         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem changing ticket state ticketId: " + ticketId, ex);
      }

   }

}
