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

package org.ni2.v01.api.tt.opennms.plugin.test.manual;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.ni2.v01.api.tt.opennms.plugin.Ni2TicketerPlugin;
import org.ni2.v01.api.tt.opennms.plugin.test.Ni2TicketerPluginTest;


public class Ni2TicketerPluginIntegrationTest {
   public static final String TEST_PROPERTIES_FILE = "test-properties.properties";
   
   private Ni2TicketerPluginTest ni2TicketerPluginTest= new Ni2TicketerPluginTest();
   
   @Before
   public void pretest() {
      System.out.println("loading properties from "+TEST_PROPERTIES_FILE);
      Properties testProperties = new Properties();
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try (InputStream stream = loader.getResourceAsStream(TEST_PROPERTIES_FILE)) {
         if (stream != null) {
            testProperties.load(stream);
            System.out.println("testproperties:"+testProperties);
         } else {
            System.out.println("cannot find " + TEST_PROPERTIES_FILE);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      ni2TicketerPluginTest.setTtServerUrl(testProperties.getProperty(Ni2TicketerPlugin.TT_SERVER_URL_PROPERTY));
      ni2TicketerPluginTest.setTtUsername(testProperties.getProperty(Ni2TicketerPlugin.TT_USERNAME_PROPERTY));
      ni2TicketerPluginTest.setTtPassword(testProperties.getProperty(Ni2TicketerPlugin.TT_PASSWORD_PROPERTY));
      ni2TicketerPluginTest.setTrustAllCertificates(testProperties.getProperty(Ni2TicketerPlugin.TT_TRUST_ALL_CERTIFICATES_PROPERTY));
      
      ni2TicketerPluginTest.setOnmsInstance(testProperties.getProperty(Ni2TicketerPlugin.TT_OPENNMS_INSTANCE_PROPERTY));

      ni2TicketerPluginTest.setFallbackResourceId(testProperties.getProperty(Ni2TicketerPlugin.TT_FALLBACK_RESOURCE_PROPERTY));
      
      ni2TicketerPluginTest.setConnectionTimeoutStr(testProperties.getProperty(Ni2TicketerPlugin.TT_CLIENT_TIMEOUT_PROPERTY));
      

      ni2TicketerPluginTest.setup();
   }
   
   @Test
   public void testTicket() {
      ni2TicketerPluginTest.testTicket();
     
   }
   

}
