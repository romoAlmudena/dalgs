<%--

    This file is part of D.A.L.G.S.

    D.A.L.G.S is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    D.A.L.G.S is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with D.A.L.G.S.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><fmt:message key="common.title" /></title>
<style>
.error {
	color: red;
}
</style>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>


	<div class="panel panel-primary group category">
		<div class="panel-heading">
			<h3 class="panel-title list">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> ${typeOfUser} &nbsp; ( ${group.course.subject.info.name} - ${group.name} )
			</h3>
		</div>
		<div class="panel-body">
			<div class="form-group">
				<div class="form-group">
					<form:form method="post" commandName="group" role="form">

						<form:hidden path="id" />
						<label><fmt:message key="group.availableUsers" /> (${typeOfUser}):</label>
						<div class="checkbox">
							<c:choose>
								<c:when test="${typeOfUser eq 'Proffesors'}">
									<form:checkboxes items="${users}" path="professors"
										itemLabel="fullName" />
									<br>
									<br>
									<br>
								</c:when>
								<c:otherwise>
									<form:checkboxes items="${users}" path="students"
										itemLabel="fullName" />
									<br>
									<br>
									<br>
								</c:otherwise>
							</c:choose>

						</div>
						<spring:message code="common.add" var="add"/>
						<input type="submit" class="btn btn-success" value="${add}"
							name="AddProfessors" />
					</form:form>
				</div>
			</div>


		</div>

	</div>


</body>
</html>
