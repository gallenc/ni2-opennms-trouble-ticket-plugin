<!-- Licensed to The OpenNMS Group, Inc (TOG) under one or more -->
<!-- contributor license agreements.  See the LICENSE.md file -->
<!-- distributed with this work for additional information -->
<!-- regarding copyright ownership. -->
<!-- TOG licenses this file to You under the GNU Affero General -->
<!-- Public License Version 3 (the "License") or (at your option) -->
<!-- any later version.  You may not use this file except in -->
<!-- compliance with the License.  You may obtain a copy of the -->
<!-- License at: -->
<!--      https://www.gnu.org/licenses/agpl-3.0.txt -->
<!-- Unless required by applicable law or agreed to in writing, -->
<!-- software distributed under the License is distributed on an -->
<!-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, -->
<!-- either express or implied.  See the License for the specific -->
<!-- language governing permissions and limitations under the -->
<!-- License. -->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="org.ni2.v01.api.tt.mock.server.Ni2TTApiV2MockImpl"%>
<%@page import="org.ni2.v01.api.tt.model.TroubleTicketEventExtended"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map.Entry"%>
<%
    Map<String, TroubleTicketEventExtended> troubleTicketDao = Ni2TTApiV2MockImpl.getTroubleTicketDao();

%>
<!DOCTYPE html>
<html>
<head>
<title>Ni2 Trouble Ticket Test Simulator</title>
<style>
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>
</head>
<body>


    <div>
        <h1>Trouble Ticket List</h1>
        <p>showing ${troubleTicketDao.size} tickets: </p>
        <table>
            <thead>
                <tr>
                    <th>universalId (ticketId)</th>
                    <th>ticket status</th>
                    <th>ticket description</th>
                    <th>alarm id</th>
                    <th>alarm severity</th>
                    <th>alarm status</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <% for( Entry<String,TroubleTicketEventExtended> entry : troubleTicketDao.entrySet()) { %>
                    <tr>
                        <td><%=entry.getKey() %></td>
                        <td><%=entry.getValue().getTTStatus() %></td>
                        <td><%=entry.getValue().getDescription() %></td>
                        <td><%=entry.getValue().getTTAlarmId() %></td>
                        <td><%=entry.getValue().getTTAlarmSeverity() %></td>
                        <td><%=entry.getValue().getTTAlarmStatus() %></td>
                        <td>
                            <form action="./TroubleTicket.jsp" method="GET">
                                <input type="hidden" name="ticketId" value="<%=entry.getKey() %>">
                                <button class="btn" type="submit" >View Ticket</button>
                            </form> 
                        </td>
                        <td><a href="../Ni2CMDBWebApi/overviewByCategoryAndAttribute/Event/UniversalId/<%=entry.getKey() %>"><%=entry.getKey() %> redirect link</a></td>
                    </tr>
                <% } %>

            </tbody>
        </table>

    </div>


</body>
</html>