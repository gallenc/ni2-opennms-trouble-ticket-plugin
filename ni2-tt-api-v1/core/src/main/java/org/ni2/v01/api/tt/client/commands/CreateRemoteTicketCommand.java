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

package org.ni2.v01.api.tt.client.commands;

import org.ni2.v01.api.tt.client.Ni2TTApiClientRawJson;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.TroubleTicketCreateRequest;
import org.ni2.v01.api.tt.model.TroubleTicketCreateResponse;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Command(scope = "ni2ticket", name = "create-remote-ticket", description = "Create a new ticket in remote system. (Note - this does not link a ticket to an alarm in OpenNMS")
public class CreateRemoteTicketCommand implements Action {

   @Argument(index = 0, name = "resourceids", description = "single resource id or comma separated list of resource ids with no spaces)", required = true, multiValued = false)
   String resources = System.getProperty(Ni2TicketerPlugin.TT_FALLBACK_RESOURCE_PROPERTY, Ni2TicketerPlugin.DEFAULT_TT_FALLBACK_RESOURCE_ID);
   
   @Option(name = "--alarmId", description = "optional integer id of associated alarm. If omitted a random Id will be created", required = false, multiValued = false)
   String alarmId =Integer.toString(ThreadLocalRandom.current().nextInt(10000, 100000 + 1));

   @Option(name = "--description", description = "optional ticket description - defaults to empty string ", required = false, multiValued = false)
   String description = "";
   
   @Option(name = "--longdescription", description = "optional ticket long description - defaults to empty string ", required = false, multiValued = false)
   String longDescription = "";
   
   @Option(name = "--alarmsource", description = "source of alarm - defaults to OpenNMS instance property", required = false, multiValued = false)
   String alarmSource = System.getProperty(Ni2TicketerPlugin.TT_OPENNMS_INSTANCE_PROPERTY, Ni2TicketerPlugin.DEFAULT_TT_OPENNMS_INSTANCE_PROPERTY);

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

   @Override
   public Object execute() throws Exception {
      System.out.println("create-remote-ticket trying to create ticket. serverUrl="+serverUrl+" username="+username + " password not shown"+ " trustAllCertificates="+trustAllCertificates);

      Ni2TTApiClient ttClient = new Ni2TTApiClientRawJson();
      ttClient.setTtServerUrl(serverUrl );
      ttClient.setTtUsername(username);
      ttClient.setTtPassword(password);
      ttClient.setTrustAllCertificates(trustAllCertificates);
      
      try {
         TroubleTicketCreateRequest troubleTicketCreateRequest = new TroubleTicketCreateRequest();
         troubleTicketCreateRequest.setAlarmId(alarmId);
         troubleTicketCreateRequest.setAlarmSource(alarmSource);
         troubleTicketCreateRequest.setDescription(description);
         troubleTicketCreateRequest.setLongDescription(longDescription);
         
         if(resources==null || resources.isBlank() || resources.contains(" ")) throw new IllegalArgumentException("resources must not be null or empty or contain spaces");
         List<String> resourceIds= Arrays.asList(resources.split(","));
         troubleTicketCreateRequest.setResourceIds(resourceIds);
         
         System.out.println("sending ticket request:"+troubleTicketCreateRequest);
         
         TroubleTicketCreateResponse response = ttClient.createTroubleTicket(troubleTicketCreateRequest);
         System.out.println("success: response="+response);
      } catch (Exception ex) {
         System.out.println("failed request create ticket: " + ex);
         ex.printStackTrace(System.out);
      }

      return null;
   }

}
