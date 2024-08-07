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

package org.ni2.v01.api.tt.mock.server.temp.heroku;

import jakarta.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.ni2.v01.api.tt.mock.server.Ni2UIRedirect;

import static org.junit.Assert.*;

/**
 * just provided as an example - not needed
 */
public class MyResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(Ni2UIRedirect.class);
    }

    /**
     * Test to see that the message "Hello, Heroku!" is sent in the response.
     */
//    @Test
//    public void testGetIt() {
//        final String responseMsg = target().path("myresource").request().get(String.class);
//
//        assertEquals("Hello, Heroku!", responseMsg);
//    }
}
