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

package org.ni2.v01.api.tt.mock.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.ni2.v01.api.tt.model.AuthenticationRequest;
import org.ni2.v01.api.tt.model.AuthenticationResponse;
import org.ni2.v01.api.tt.model.ErrorResponse;
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.Ni2TTStatus;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;

@Path("/api")
public class Ni2TTApiV2MockImpl {
   private static final Logger LOG = LoggerFactory.getLogger(Ni2TTApiV2MockImpl.class);

   public static final int MAXIMUM_TICKETS_IN_LIST = 100;

   private static final AtomicInteger currentTicketID = new AtomicInteger(1);

   // <universalId, ticket>
   private static final Map<String, TroubleTicketEventExtended> troubleTicketDao = Collections.synchronizedMap(new LinkedHashMap<String, TroubleTicketEventExtended>());

   public Map<String, TroubleTicketEventExtended> getTroubleTicketDao() {
      return troubleTicketDao;
   }

   private String getTicketId() {
      Integer id = currentTicketID.getAndIncrement();

      return "EVT-" + String.format("%08d", id);
   }

   /**
    * post {{baseUrl}}/api/v1/auth/login
    * payload: 
    *   {
    *     "username": "{{username}}",
    *     "password": "{{password}}"
    *   }
    *
    * @return authtoken
    */
   @Path("/v1/auth/login")
   @POST
   @Consumes({ "application/json" })
   @Produces({ "application/json" })
   public Response authenticationControllerLogin(AuthenticationRequest authenticationRequest, @Context HttpServletRequest httpRequest) {
      String requestURI = httpRequest.getRequestURI();
      LOG.warn("calling authenticationControllerLogin authenticationRequest: {} requestURI: {}", authenticationRequest, requestURI);

      AuthenticationResponse authenticationResponse = new AuthenticationResponse();
      authenticationResponse.setAccessToken(UUID.randomUUID().toString() + "_" + authenticationRequest.getUsername());

      if (authenticationRequest == null
               || authenticationRequest.getUsername() == null || authenticationRequest.getUsername().isBlank()
               || authenticationRequest.getPassword() == null || authenticationRequest.getPassword().isBlank()) {

         Status status = Response.Status.UNAUTHORIZED;
         String code=status.name();
         String reason = "username unauthorised:"+authenticationRequest.getUsername();
         
         ErrorResponse errorResponse = new ErrorResponse();
         errorResponse.setCode(code);
         errorResponse.setStatusCode(status.getStatusCode());
         errorResponse.setDescription(status.getReasonPhrase());
         errorResponse.setDescription(reason);
         errorResponse.setPath(requestURI);
         errorResponse.setTimestampDate(new Date());

         Response response = Response
                  .status(Response.Status.UNAUTHORIZED)
                  .entity(errorResponse)
                  .build();
         return response;
      }

      Response response = Response
               .status(Response.Status.OK)
               .entity(authenticationResponse)
               .build();
      return response;
   };

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
   @Path("/v1/entity/event/create/event")
   @POST
   @Consumes({ "application/json" })
   @Produces({ "application/json" })
   public Response createTroubleTicket(TroubleTicketCreateRequest troubleTicketCreateRequest, @HeaderParam("Authorization") String authorisation, @Context HttpServletRequest httpRequest) {
      String requestURI = httpRequest.getRequestURI();
      LOG.warn("calling createTroubleTicket troubleTicketCreateRequest: {} Authorization Header value {} requestURI: {}", troubleTicketCreateRequest, authorisation, requestURI);

      Status status = null;
      String reason = null;
      TroubleTicketCreateResponse responseMessage = new TroubleTicketCreateResponse();

      if (!this.checkAuthorisation(authorisation)) {
         status = Response.Status.UNAUTHORIZED;
         reason = status.getReasonPhrase();
      } else {

         // create new ticket
         TroubleTicketEventExtended tticket = new TroubleTicketEventExtended();
         String ticketId = getTicketId();

         tticket.setStatus(Ni2TTStatus.OPEN);
         tticket.setUniversalId(ticketId);

         tticket.setClassificationPath("Event(\"Event/Support/Incident/Monitoring Incident\")");
         tticket.setAlarmId(troubleTicketCreateRequest.getAlarmId());
         tticket.setDescription(troubleTicketCreateRequest.getDescription());
         tticket.setLongDescription(troubleTicketCreateRequest.getLongDescription());

         tticket.setAlarmSource(troubleTicketCreateRequest.getAlarmSource());

         tticket.setTTCategory(troubleTicketCreateRequest.getTTCategory());
         tticket.setResourceIds(troubleTicketCreateRequest.getResourceIds());

         // add ticket to dao
         troubleTicketDao.put(ticketId, tticket);

         // remote dao entry beyond size
         if (troubleTicketDao.size() >= MAXIMUM_TICKETS_IN_LIST) {
            List<String> keys = List.copyOf(troubleTicketDao.keySet());
            String lastEntry = keys.get(keys.size() - 1);
            troubleTicketDao.remove(lastEntry);
         }

         responseMessage = new TroubleTicketCreateResponse();
         responseMessage.setUniversalId(ticketId);
         responseMessage.setUrl("/api/v1/entity/event/get/event/base/" + ticketId);
         status = Response.Status.OK;
      }

      if (status.getStatusCode() >= Response.Status.BAD_REQUEST.getStatusCode()) {

         
         ErrorResponse errorResponse = new ErrorResponse();
         errorResponse.setCode(status.name());
         errorResponse.setStatusCode(status.getStatusCode());
         errorResponse.setDescription(status.getReasonPhrase());
         errorResponse.setDescription(reason);
         errorResponse.setPath(requestURI);
         errorResponse.setTimestampDate(new Date());

         Response response = Response
                  .status(status)
                  .entity(errorResponse)
                  .build();
         return response;
      } else {

         Response response = Response
                  .status(status)
                  .entity(responseMessage)
                  .build();
         return response;
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
    */
   @Path("/v1/entity/event/get/event/extended/{eventId}")
   @POST
   @Consumes({ "application/json" })
   @Produces({ "application/json" })
   public Response getTroubleTicket(@PathParam("eventId") String ticketId, @HeaderParam("Authorization") String authorisation, @Context HttpServletRequest httpRequest) {
      String requestURI = httpRequest.getRequestURI();
      LOG.warn("getTroubleTicket ticketId: {} Authorization Header value {} requestURI: {}", ticketId, authorisation, requestURI);

      Status status = null;
      String reason = null;
      TroubleTicketEventExtended responseMessage = null;

      if (!this.checkAuthorisation(authorisation)) {
         status = Response.Status.UNAUTHORIZED;
         reason = status.getReasonPhrase();

      } else if (ticketId == null || ticketId.isBlank()) {
         status = Response.Status.BAD_REQUEST;
         reason = "ticketId is empty";

      } else {

         responseMessage = troubleTicketDao.get(ticketId);
         if (responseMessage == null) {
            status = Response.Status.NOT_FOUND;
            reason = "ticket not found ticketId=" + ticketId;
         } else {
            status = Response.Status.OK;
         }

      }

      if (status.getStatusCode() >= Response.Status.BAD_REQUEST.getStatusCode()) {

         ErrorResponse errorResponse = new ErrorResponse();
         
         errorResponse.setCode(status.name());
         errorResponse.setStatusCode(status.getStatusCode());
         errorResponse.setDescription(status.getReasonPhrase());
         errorResponse.setDescription(reason);
         errorResponse.setPath(requestURI);
         errorResponse.setTimestampDate(new Date());

         Response response = Response
                  .status(status)
                  .entity(errorResponse)
                  .build();
         return response;
      } else {

         Response response = Response
                  .status(status)
                  .entity(responseMessage)
                  .build();
         return response;
      }

   }

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
   @Path("/v1/entity/event/executeLifecycleAction/event/{eventId}")
   @POST
   @Consumes({ "application/json" })
   @Produces({ "application/json" })
   public Response changeTicketState(@PathParam("eventId") String ticketId, LifecycleActionRequest lifecycleActionRequest, @HeaderParam("Authorization") String authorisation,
            @Context HttpServletRequest httpRequest) {
      String requestURI = httpRequest.getRequestURI();

      LOG.warn("calling changeTicketState ticketId: {} action {} comment: {} lifecycleActionRequest: {} Authorization Header value {} requestURI: {}", ticketId,
               lifecycleActionRequest.getAction(), lifecycleActionRequest.getComment(), lifecycleActionRequest, authorisation, requestURI);

      Status status = null;
      String reason = null;

      if (!this.checkAuthorisation(authorisation)) {
         status = Response.Status.UNAUTHORIZED;
         reason = status.getReasonPhrase();
      } else if (ticketId == null || ticketId.isBlank()) {
         status = Response.Status.BAD_REQUEST;
         reason = "ticketId is empty";

      } else {

         TroubleTicketEventExtended ticket = troubleTicketDao.get(ticketId);
         if (ticket == null) {
            status = Response.Status.NOT_FOUND;
            reason = "ticket not found ticketId=" + ticketId;
         } else {
            String action = lifecycleActionRequest.getAction();
            if (!LifecycleActionRequest.ALLOWED_ACTIONS_LIST.contains(action)) {
               status = Response.Status.BAD_REQUEST;
               reason = "action is not known";
            } else {

               // todo check status and update
               switch (action) {
               case LifecycleActionRequest.CANCEL:
                  ticket.setStatus(Ni2TTStatus.CANCELED);
                  status = Response.Status.OK;
                  break;
               case LifecycleActionRequest.CLOSE:
                  ticket.setStatus(Ni2TTStatus.CLOSED);
                  status = Response.Status.OK;
                  break;
               case LifecycleActionRequest.RESOLVE:
                  ticket.setStatus(Ni2TTStatus.RESOLVED);
                  status = Response.Status.OK;
                  break;
               }

               
            }
         }

      }

      if (status.getStatusCode() >= Response.Status.BAD_REQUEST.getStatusCode()) {

         ErrorResponse errorResponse = new ErrorResponse();
         
         errorResponse.setCode(status.name());
         errorResponse.setStatusCode(status.getStatusCode());
         errorResponse.setDescription(status.getReasonPhrase());
         errorResponse.setDescription(reason);
         errorResponse.setPath(requestURI);
         errorResponse.setTimestampDate(new Date());

         Response response = Response
                  .status(status)
                  .entity(errorResponse)
                  .build();
         return response;
      } else {

         Response response = Response
                  .status(Response.Status.OK)
                  .build();
         return response;
      }

   }

   private boolean checkAuthorisation(String authorisationHeader) {
      return true;
   }

}
