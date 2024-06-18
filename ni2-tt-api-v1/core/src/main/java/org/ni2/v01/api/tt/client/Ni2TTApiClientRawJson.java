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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


import org.ni2.v01.api.tt.model.AuthenticationRequest;
import org.ni2.v01.api.tt.model.AuthenticationResponse;
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.Ni2ClientException;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;


public class Ni2TTApiClientRawJson implements Ni2TTApiClient {

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
    * @throws IOException 
    * @throws KeyManagementException 
    * @throws NoSuchAlgorithmException 
   */
   HttpURLConnection getHttpURLConnection(String requestUrl) throws IOException, KeyManagementException, NoSuchAlgorithmException {
      if (requestUrl == null || requestUrl.isBlank())
         throw new IllegalArgumentException("requestUrl must not be null or empty");
      if (ttUsername == null || ttUsername.isBlank())
         throw new IllegalArgumentException("ttUsername must not be null or empty");
      if (ttPassword == null)
         throw new IllegalArgumentException("ttPassword must not be null");

      HttpURLConnection httpUrlConnection = null;

      URL url = new URL(requestUrl);

      if ("https".equals(url.getProtocol())) {

         HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

         if (trustAllCertificates) {
            con.setHostnameVerifier(new HostnameVerifier() {
               public boolean verify(String hostname, SSLSession session) {
                  return true;
               }
            });

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
               public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
               }

               public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
               }

               public X509Certificate[] getAcceptedIssuers() {
                  return new X509Certificate[0];
               }
            } }, new SecureRandom());

            con.setSSLSocketFactory(context.getSocketFactory());
         }

         httpUrlConnection = (HttpURLConnection) con;

      } else if ("http".equals(url.getProtocol())) {
         httpUrlConnection = (HttpURLConnection) url.openConnection();

      } else {
         throw new IllegalArgumentException("unsupported protocol in ttServerUrl " + ttServerUrl);
      }

      httpUrlConnection.setRequestMethod("POST");
      httpUrlConnection.setRequestProperty("Content-Type", "application/json");
      httpUrlConnection.setRequestProperty("Accept", "application/json");
      httpUrlConnection.setDoOutput(true);
      httpUrlConnection.setDoInput(true);

      return httpUrlConnection;
   }


   /**
    * post {{baseUrl}}/api/v1/auth/login
    * payload: 
    *   {
    *     "username": "{{username}}",
    *     "password": "{{password}}"
    *   }
    *
    * @return auth-token
    */
   @Override
   public String getAuthenticationToken() throws Ni2ClientException {
      if (ttServerUrl == null || ttServerUrl.isBlank())
         throw new IllegalArgumentException("ttServerUrl must not be null or empty");

      String requestUrl = ttServerUrl + "/api/v1/auth/login";
      String content = null;

      try {

         HttpURLConnection httpURLConnection = getHttpURLConnection(requestUrl);

         JsonMapper mapper = new JsonMapper();
         mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

         AuthenticationRequest authenticationRequest = new AuthenticationRequest();
         authenticationRequest.setPassword(ttPassword);
         authenticationRequest.setUsername(ttUsername);

         StringWriter stringwriter = new StringWriter();
         mapper.writeValue(stringwriter, authenticationRequest);
         String requestBody = stringwriter.toString();
         LOG.debug("POST requestUrl=" + requestUrl + " requestBody: " + requestBody);

         try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] output = requestBody.toString().getBytes("utf-8");
            os.write(output, 0, output.length);
         }

         int responseCode = httpURLConnection.getResponseCode();
         LOG.debug("POST Response Code :: " + responseCode);

         if (responseCode == HttpURLConnection.HTTP_OK) { //success

            // read input stream
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
               StringBuilder response = new StringBuilder();
               String responseLine = null;
               while ((responseLine = br.readLine()) != null) {
                  response.append(responseLine.trim());
               }
               content = response.toString();
            }

            LOG.debug("POST Response content: " + content);

            AuthenticationResponse authResponse = mapper.readValue(content, AuthenticationResponse.class);
            return authResponse.getAccessToken();

         } else {
            // read error stream
            String error = null;
            if (httpURLConnection.getErrorStream() != null) {
               try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                     response.append(responseLine.trim());
                  }
                  error = response.toString();
               }
            }

            throw new Ni2ClientException("failed Authentication login " + requestUrl
                     + " for username=" + ttUsername
                     + " error response:" + error);
         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem authenticating with " + requestUrl, ex);
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
      if (ttServerUrl == null || ttServerUrl.isBlank())
         throw new IllegalArgumentException("ttServerUrl must not be null or empty");

      String requestUrl = ttServerUrl + "/api/v1/entity/event/create/event";
      String content = null;

      try {
         
         String authenticationToken = getAuthenticationToken();

         HttpURLConnection httpURLConnection = getHttpURLConnection(requestUrl);
         httpURLConnection.setRequestProperty("Authorization", "Bearer " + authenticationToken);

         JsonMapper mapper = new JsonMapper();
         mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

         StringWriter stringwriter = new StringWriter();
         mapper.writeValue(stringwriter, createRequest);
         String requestBody = stringwriter.toString();
         LOG.debug("POST requestUrl=" + requestUrl + " requestBody: " + requestBody);

         try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] output = requestBody.toString().getBytes("utf-8");
            os.write(output, 0, output.length);
         }

         int responseCode = httpURLConnection.getResponseCode();
         LOG.debug("POST Response Code :: " + responseCode);

         if (responseCode == HttpURLConnection.HTTP_OK) { //success

            // read input stream
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
               StringBuilder response = new StringBuilder();
               String responseLine = null;
               while ((responseLine = br.readLine()) != null) {
                  response.append(responseLine.trim());
               }
               content = response.toString();
            }

            LOG.debug("POST Response content: " + content);
            
            TroubleTicketCreateResponse troubleTicketCreateResponse = mapper.readValue(content, TroubleTicketCreateResponse.class);
            return troubleTicketCreateResponse;

         } else {
            // read error stream
            String error = null;
            if (httpURLConnection.getErrorStream() != null) {
               try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                     response.append(responseLine.trim());
                  }
                  error = response.toString();
               }
            }

            throw new Ni2ClientException("failed creating trouble ticket " + requestUrl
                     + " for username=" + ttUsername
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

      if (ttServerUrl == null || ttServerUrl.isBlank())
         throw new IllegalArgumentException("ttServerUrl must not be null or empty");
      if (ticketId == null ||  ticketId.isBlank())
         throw new IllegalArgumentException("ticketId must not be null or empty");

      String requestUrl = ttServerUrl + "/api/v1/entity/event/get/event/extended/"+ticketId;
      String content = null;

      try {
         
         String authenticationToken = getAuthenticationToken();

         HttpURLConnection httpURLConnection = getHttpURLConnection(requestUrl);
         httpURLConnection.setRequestProperty("Authorization", "Bearer " + authenticationToken);

         JsonMapper mapper = new JsonMapper();
         mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

         String requestBody = "";
         LOG.debug("POST requestUrl=" + requestUrl + " requestBody: " + requestBody);

         try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] output = requestBody.toString().getBytes("utf-8");
            os.write(output, 0, output.length);
         }

         int responseCode = httpURLConnection.getResponseCode();
         LOG.debug("POST Response Code :: " + responseCode);

         if (responseCode == HttpURLConnection.HTTP_OK) { //success

            // read input stream
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
               StringBuilder response = new StringBuilder();
               String responseLine = null;
               while ((responseLine = br.readLine()) != null) {
                  response.append(responseLine.trim());
               }
               content = response.toString();
            }

            LOG.debug("POST Response content: " + content);
            
            TroubleTicketEventExtended troubleTicketEventExtendedResponse = mapper.readValue(content, TroubleTicketEventExtended.class);
            return troubleTicketEventExtendedResponse;

         } else {
            // read error stream
            String error = null;
            if (httpURLConnection.getErrorStream() != null) {
               try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                     response.append(responseLine.trim());
                  }
                  error = response.toString();
               }
            }

            throw new Ni2ClientException("failed getting trouble ticket " + requestUrl
                     + " for username=" + ttUsername
                     + " error response:" + error);
         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem getting ticketId: " + ticketId, ex);
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
   @Override
   public void changeTicketState(String ticketId, String action, String comment) throws Ni2ClientException {
      LOG.debug("calling changeTicketState ticketId: {} action {} comment: {} ", ticketId, action, comment);
      
      if (ttServerUrl == null || ttServerUrl.isBlank())
         throw new IllegalArgumentException("ttServerUrl must not be null or empty");

      if (comment == null)
         throw new IllegalArgumentException("comment must be set");

      if (!LifecycleActionRequest.ALLOWED_ACTIONS_LIST.contains(action)) {
         throw new IllegalArgumentException("action " + action + "must be one of " + LifecycleActionRequest.ALLOWED_ACTIONS_LIST);
      }
      
      String requestUrl = ttServerUrl + "/api/v1/entity/event/executeLifecycleAction/event/"+ticketId;

      try {
         
         String authenticationToken = getAuthenticationToken();

         HttpURLConnection httpURLConnection = getHttpURLConnection(requestUrl);
         httpURLConnection.setRequestProperty("Authorization", "Bearer " + authenticationToken);

         JsonMapper mapper = new JsonMapper();
         mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
         
         LifecycleActionRequest lifecycleActionRequest = new LifecycleActionRequest();
         lifecycleActionRequest.setAction(action);
         lifecycleActionRequest.setComment(comment);
         
         StringWriter stringwriter = new StringWriter();
         mapper.writeValue(stringwriter, lifecycleActionRequest);
         String requestBody = stringwriter.toString();

         LOG.debug("POST requestUrl=" + requestUrl + " requestBody: " + requestBody);

         try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] output = requestBody.toString().getBytes("utf-8");
            os.write(output, 0, output.length);
         }

         int responseCode = httpURLConnection.getResponseCode();
         LOG.debug("POST Response Code :: " + responseCode);

         if (responseCode == HttpURLConnection.HTTP_OK) { //success

            LOG.debug("success changeTicketState ticketId: {} action {} comment: {} ", ticketId, action, comment);

         } else {
            // read error stream
            String error = null;
            if (httpURLConnection.getErrorStream() != null) {
               try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"))) {
                  StringBuilder response = new StringBuilder();
                  String responseLine = null;
                  while ((responseLine = br.readLine()) != null) {
                     response.append(responseLine.trim());
                  }
                  error = response.toString();
               }
            }

            throw new Ni2ClientException("failed change ticket state " + requestUrl
                     + " for username=" + ttUsername
                     + " error response:" + error);
         }

      } catch (Exception ex) {
         throw new Ni2ClientException("problem changing ticket state ticketId: " + ticketId, ex);
      }

   }

}
