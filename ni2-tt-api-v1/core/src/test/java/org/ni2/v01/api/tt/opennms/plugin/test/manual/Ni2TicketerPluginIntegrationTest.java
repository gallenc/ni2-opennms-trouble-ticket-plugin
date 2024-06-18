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

package org.ni2.v01.api.tt.opennms.plugin.test.manual;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

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

      ni2TicketerPluginTest.setTtServerUrl(testProperties.getProperty("ni2.tt.server.url"));
      ni2TicketerPluginTest.setTtUsername(testProperties.getProperty("ni2.tt.server.username"));
      ni2TicketerPluginTest.setTtPassword(testProperties.getProperty("ni2.tt.server.password"));
      ni2TicketerPluginTest.setTrustAllCertificates(testProperties.getProperty("ni2.tt.server.trustallcertificates"));
      
      ni2TicketerPluginTest.setOnmsInstance(testProperties.getProperty("ni2.tt.opennms.instance"));
      
      ni2TicketerPluginTest.setFallbackResourceId(testProperties.getProperty("ni2.tt.opennms.fallbackresource"));

      ni2TicketerPluginTest.setup();
   }
   
   @Test
   public void testTicket() {
      ni2TicketerPluginTest.testTicket();
     
   }
   

}
