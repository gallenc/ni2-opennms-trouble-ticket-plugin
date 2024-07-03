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
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;



@Service
@Command(scope = "ni2-ticketing", name = "get-auth-token", description = "Get authentication token")
public class GetAuthenticationCommand implements Action {

   //    @Argument(index = 0, name = "username", description = "Booking ID", required = true, multiValued = false)
   //    long id;

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
      System.out.println("get-auth-token trying to get auth token serverUrl="+serverUrl+" username="+username + " password not shown"+ " trustAllCertificates="+trustAllCertificates);
      Ni2TTApiClient ttClient = new Ni2TTApiClientRawJson();
      ttClient.setTtServerUrl(serverUrl );
      ttClient.setTtUsername(username);
      ttClient.setTtPassword(password);
      ttClient.setTrustAllCertificates( trustAllCertificates);
      
      Integer timeOut = Integer.parseInt(timeoutStr);
      ttClient.setConnectionTimeout(timeOut);
      
      try {
         String authToken = ttClient.getAuthenticationToken();
         System.out.println("received Authentication Token: " + authToken);
      } catch (Exception ex) {
         System.out.println("failed request for Authentication Token: " + ex);
         ex.printStackTrace(System.out);
      }

      return null;
   }

}
