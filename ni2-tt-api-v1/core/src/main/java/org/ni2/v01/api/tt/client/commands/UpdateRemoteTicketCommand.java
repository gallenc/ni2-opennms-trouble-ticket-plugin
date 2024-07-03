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

package org.ni2.v01.api.tt.client.commands;

import org.ni2.v01.api.tt.client.Ni2TTApiClientRawJson;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.model.TroubleTicketUpdateRequest;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Command(scope = "ni2-ticketing", name = "update-remote-ticket", description = "Update an existing ticket in remote system. (Note - this does not link a ticket to a real alarm in OpenNMS")
public class UpdateRemoteTicketCommand implements Action {
   
   @Argument(index = 0, name = "tticketId", description = "trouble ticket id", required = true, multiValued = false)
   String tticketId;
   
   @Option(name = "--alarmId", description = "optional integer id of associated alarm. If omitted a random Id will be created", required = false, multiValued = false)
   String alarmId = null;

   @Option(name = "--description", description = "optional ticket description - defaults to empty string ", required = false, multiValued = false)
   String description =  null;
   
   @Option(name = "--longdescription", description = "optional ticket long description - defaults to empty string ", required = false, multiValued = false)
   String longDescription = null;
   
   @Option(name = "--alarmsource", description = "source of alarm - defaults to OpenNMS instance property", required = false, multiValued = false)
   String alarmSource = null;
   
   @Option(name = "--alarmseverity", description = "severity of alarm (one of Indeterminate,Cleared,Normal,Warning,Minor,Major,Critical) defaults to Minor", required = false, multiValued = false)
   String alarmSeverity = null;
   
   @Option(name = "--alarmstatus", description = "status of alarm alarm - Acknowledged or Unacknowledged (default)", required = false, multiValued = false)
   String alarmStatus = null;

   @Option(name = "--ttcategory", description = "category of trouble ticket", required = false, multiValued = false)
   String ttcategory = null;

   @Option(name = "--url", description = "Location of the ni2 trouble ticket service - defaults to OpenNMS property "
            + Ni2TicketerPlugin.TT_SERVER_URL_PROPERTY, required = false, multiValued = false)
   String serverUrl = System.getProperty(Ni2TicketerPlugin.TT_SERVER_URL_PROPERTY, Ni2TicketerPlugin.DEFAULT_TT_SERVER_URL_PROPERTY);

   @Option(name = "--username", description = "Username - defaults to OpenNMS property "
            + Ni2TicketerPlugin.TT_USERNAME_PROPERTY, required = false, multiValued = false)
   String username = System.getProperty(Ni2TicketerPlugin.TT_USERNAME_PROPERTY, Ni2TicketerPlugin.DEFAULT_TT_USERNAME_PROPERTY);

   @Option(name = "--password", description = "Password - defaults to OpenNMS property "
            + Ni2TicketerPlugin.TT_PASSWORD_PROPERTY, required = false, multiValued = false)
   String password = System.getProperty(Ni2TicketerPlugin.TT_PASSWORD_PROPERTY, Ni2TicketerPlugin.DEFAULT_TT_PASSWORD_PROPERTY);
   
   @Option(name = "--trustAllCertificates", description = "if true self signed certificates are trusted - defaults to OpenNMS property "
            + Ni2TicketerPlugin.TT_PASSWORD_PROPERTY, required = false, multiValued = false)
   boolean trustAllCertificates = Boolean.valueOf(System.getProperty(Ni2TicketerPlugin.TT_TRUST_ALL_CERTIFICATES_PROPERTY, Ni2TicketerPlugin.DEFAULT_TT_TRUST_ALL_CERTIFICATES_PROPERTY));
   
   @Option(name = "--timeout", description = "client timeout (ms) - defaults to OpenNMS property "
            + Ni2TicketerPlugin.TT_PASSWORD_PROPERTY, required = false, multiValued = false)
   String timeoutStr = System.getProperty(Ni2TicketerPlugin.TT_CLIENT_TIMEOUT_PROPERTY, Ni2TicketerPlugin.DEFAULT_CLIENT_TIMEOUT);

   @Override
   public Object execute() throws Exception {
      System.out.println("create-remote-ticket trying to create ticket. serverUrl="+serverUrl+" username="+username + " password not shown"+ " trustAllCertificates="+trustAllCertificates);

      Ni2TTApiClient ttClient = new Ni2TTApiClientRawJson();
      ttClient.setTtServerUrl(serverUrl );
      ttClient.setTtUsername(username);
      ttClient.setTtPassword(password);
      ttClient.setTrustAllCertificates(trustAllCertificates);
      
      Integer timeOut = Integer.parseInt(timeoutStr);
      ttClient.setConnectionTimeout(timeOut);
      
      try {
         TroubleTicketUpdateRequest troubleTicketUpdateRequest = new TroubleTicketUpdateRequest();
         if( alarmId!=null ) troubleTicketUpdateRequest.setAlarmId(alarmId);
         if( alarmSource!=null ) troubleTicketUpdateRequest.setAlarmSource(alarmSource);
         if( description!=null ) troubleTicketUpdateRequest.setDescription(description);
         if( longDescription!=null ) troubleTicketUpdateRequest.setLongDescription(longDescription);
         if( alarmSeverity!=null ) troubleTicketUpdateRequest.setAlarmSeverity(alarmSeverity);
         if( alarmStatus!=null ) troubleTicketUpdateRequest.setAlarmStatus(alarmStatus);
         if( ttcategory!=null ) troubleTicketUpdateRequest.setTTCategory(ttcategory);
         
         System.out.println("sending ticket update request:"+troubleTicketUpdateRequest);
         
         ttClient.updateTroubleTicket(tticketId, troubleTicketUpdateRequest);
         
         System.out.println("success: updated ticket tticketId="+tticketId );
         
      } catch (Exception ex) {
         System.out.println("failed request create ticket: " + ex);
         ex.printStackTrace(System.out);
      }

      return null;
   }

}
