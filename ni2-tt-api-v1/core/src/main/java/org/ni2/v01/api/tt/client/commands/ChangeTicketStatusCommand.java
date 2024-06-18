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
import org.ni2.v01.api.tt.model.LifecycleActionRequest;
import org.ni2.v01.api.tt.model.Ni2TTApiClient;
import org.ni2.v01.api.tt.model.TroubleTicketEventExtended;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Service
@Command(scope = "ni2ticket", name = "change-status", description = "change status of ticket")
public class ChangeTicketStatusCommand implements Action {

   @Argument(index = 0, name = "tticketId", description = "trouble ticket id", required = true, multiValued = false)
   String tticketId;

   @Argument(index = 1, name = "status", description = "requested change of status. (Must be one of 'Close', 'Cancel', 'Resolve')", required = true, multiValued = false)
   String status;

   @Option(name = "--comment", description = "optional comment for change state - defaults to empty string ", required = false, multiValued = false)
   String comment = "";

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
      System.out.println("change-status trying to change ticket tticketId="+tticketId +" to "+status
               + " serverUrl="+serverUrl+" username="+username + " password not shown"+ " trustAllCertificates="+trustAllCertificates);

      Ni2TTApiClient ttClient = new Ni2TTApiClientRawJson();
      ttClient.setTtServerUrl(serverUrl );
      ttClient.setTtUsername(username);
      ttClient.setTtPassword(password);
      ttClient.setTrustAllCertificates( trustAllCertificates);
      
      try {
         ttClient.changeTicketState(tticketId, status, comment);
         System.out.println("success: ");
      } catch (Exception ex) {
         System.out.println("failed request to change ticket state: " + ex);
         ex.printStackTrace(System.out);
      }

      return null;
   }

}
