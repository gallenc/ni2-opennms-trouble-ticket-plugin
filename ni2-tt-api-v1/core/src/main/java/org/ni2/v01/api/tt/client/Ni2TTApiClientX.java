package org.ni2.v01.api.tt.client;

import org.apache.cxf.jaxrs.client.WebClient;
import org.ni2.v01.api.tt.model.Ni2ClientException;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;

public interface Ni2TTApiClientX {

   void setTtServerUrl(String ttServerUrl);

   void setTtUsername(String ttUsername);

   void setTtPassword(String ttPassword);

   /**
    * creates new web client with trust any TLS
    * @return
    */

   WebClient getWebClient();

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
   String getAuthenticationToken() throws Ni2ClientException;

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
   TroubleTicketCreateResponse createTroubleTicket(TroubleTicketCreateRequest createRequest) throws Ni2ClientException;

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
   TroubleTicketEventExtended getTroubleTicket(String ticketId) throws Ni2ClientException;

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
   void changeTicketState(String ticketId, String action, String comment) throws Ni2ClientException;

}