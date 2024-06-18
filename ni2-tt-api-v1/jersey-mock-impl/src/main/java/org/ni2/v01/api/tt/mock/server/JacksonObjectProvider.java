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

import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
//@Produces(MediaType.APPLICATION_JSON)
public class JacksonObjectProvider implements ContextResolver<ObjectMapper> {

   final ObjectMapper defaultObjectMapper;

   public JacksonObjectProvider() {
      System.out.println("object provider registered: "+JacksonObjectProvider.class.getName());
      defaultObjectMapper = createDefaultMapper();
   }

   @Override
   public ObjectMapper getContext(Class<?> type) {
      return defaultObjectMapper;
   }

   private static ObjectMapper createDefaultMapper() {
      final ObjectMapper result = new ObjectMapper();
      result.enable(SerializationFeature.INDENT_OUTPUT);

      return result;
   }


}