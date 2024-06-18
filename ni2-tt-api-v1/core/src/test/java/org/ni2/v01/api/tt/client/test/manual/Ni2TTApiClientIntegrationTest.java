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

package org.ni2.v01.api.tt.client.test.manual;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.ni2.v01.api.tt.client.test.Ni2TTApiClientTest;
import org.ni2.v01.api.tt.model.Ni2ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * runs tests in Ni2TTApiClientTest against a real system with url and credentials set in 
 * test/resources/test-properties.properties
 */
public class Ni2TTApiClientIntegrationTest {
   private static final Logger LOG = LoggerFactory.getLogger(Ni2TTApiClientIntegrationTest.class);
   
   public static final String TEST_PROPERTIES_FILE = "test-properties.properties";
   
   private Ni2TTApiClientTest ni2TTApiClientTest= new Ni2TTApiClientTest();
   
   @Before
   public void pretest() {
      LOG.debug("loading properties from "+TEST_PROPERTIES_FILE);
      Properties testProperties = new Properties();
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try (InputStream stream = loader.getResourceAsStream(TEST_PROPERTIES_FILE)) {
         if (stream != null) {
            testProperties.load(stream);
            LOG.debug("testproperties:"+testProperties);
         } else {
            LOG.debug("cannot find " + TEST_PROPERTIES_FILE);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      ni2TTApiClientTest.setTtServerUrl(testProperties.getProperty("ni2.tt.server.url"));
      ni2TTApiClientTest.setTtUsername(testProperties.getProperty("ni2.tt.server.username"));
      ni2TTApiClientTest.setTtPassword(testProperties.getProperty("ni2.tt.server.password"));  
      
      ni2TTApiClientTest.setOnmsInstance(testProperties.getProperty("ni2.tt.opennms.instance"));
      
      ni2TTApiClientTest.setFallbackResourceId(testProperties.getProperty("ni2.tt.opennms.fallbackresource"));

      ni2TTApiClientTest.setup();
   }
   
   @Test
   public void testAuthentication() throws Ni2ClientException {
      ni2TTApiClientTest.testAuthentication();

   }
   
   @Test
   public void testCreateGetAndUpdateTicket() throws Ni2ClientException {
      ni2TTApiClientTest.testCreateGetAndUpdateTicket();
   }
   

}
