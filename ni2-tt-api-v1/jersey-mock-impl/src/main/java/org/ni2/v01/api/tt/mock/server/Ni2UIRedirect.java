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

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * redirects to the ticket simulator JSP
 */
@Path("/")
public class Ni2UIRedirect {

   private static final Logger LOG = LoggerFactory.getLogger(Ni2UIRedirect.class);

   @Path("")
   @GET
   public Response getroot() throws URISyntaxException {

      LOG.warn("calling getroot");

      URI targetURIForRedirection = new URI("/Ni2TTSimulatorUI/index.html");

      LOG.warn("redirect uri: {}", targetURIForRedirection.getPath());

      return Response.temporaryRedirect(targetURIForRedirection).build();
   }

   @Path("/Ni2CMDBWebApi/overviewByCategoryAndAttribute/Event/UniversalId/{id}")
   @GET
   public Response getUITroubleTicket(@PathParam("id") String ticketId) throws URISyntaxException {

      LOG.warn("calling getUITroubleTicket ticketID: {}", ticketId);

      URI targetURIForRedirection = new URI("../Ni2TTSimulatorUI/TroubleTicket.jsp?ticketId=" + ticketId);

      LOG.warn("redirect uri: {}", targetURIForRedirection.getPath());

      return Response.temporaryRedirect(targetURIForRedirection).build();
   }
}
