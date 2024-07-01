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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="org.ni2.v01.api.tt.mock.server.Ni2TTApiV2MockImpl"%>
<%@page import="org.ni2.v01.api.tt.model.TroubleTicketEventExtended"%>
<%@page import="java.util.Map"%>
<%
Map<String, TroubleTicketEventExtended> troubleTicketDao = Ni2TTApiV2MockImpl.getTroubleTicketDao();
String ticketId = request.getParameter("ticketId");
TroubleTicketEventExtended tticket = troubleTicketDao.get(ticketId);
if (tticket == null)
   tticket = new TroubleTicketEventExtended();
%>
<!DOCTYPE html>
<html>
<head>
<title>Ni2 Trouble Ticket simulator</title>
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
	<h1>Trouble Ticket</h1>

	<a
		href="../Ni2CMDBWebApi/overviewByCategoryAndAttribute/Event/UniversalId/<%=ticketId%>"><%=ticketId%>
		redirect link</a>

	<div>
		<table>
			<tr>
				<td>universalId (ticketId)</td>
				<td><%=ticketId%></td>
			</tr>

			<tr>
				<td>ticket description</td>
				<td><%=tticket.getDescription()%></td>
			</tr>
			<tr>
				<td>ticket long description</td>
				<td><%=tticket.getLongDescription()%></td>
			</tr>
			<tr>
				<td>ticket status</td>
				<td><%=tticket.getTTStatus()%></td>
			</tr>
			<tr>
				<td>alarm id</td>
				<td><%=tticket.getTTAlarmId()%></td>
			</tr>
			<tr>
				<td>alarm severity</td>
				<td><%=tticket.getTTAlarmSeverity()%></td>
			</tr>
			<tr>
				<td>alarm status</td>
				<td><%=tticket.getTTAlarmStatus()%></td>
			</tr>

		</table>

	</div>

	<h1>Raw Trouble Ticket</h1>
	<p><%=tticket%></p>

	<a href="./TroubleTicketList.jsp">Return to ticket list</a>

</body>
</html>