<%@ include file="/WEB-INF/views/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><fmt:message key="title" /></title>
<style>
.error {
	color: red;
}
</style>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

	<div class="panel panel-primary group">
		<div class="panel-heading">
			<span class="glyphicon glyphicon-paperclip" aria-hidden="true">&nbsp;</span>
			<h3 class="panel-title list">
			Subject Details</h3>
			<sec:authorize access="hasRole('ROLE_ADMIN')">
			<a class="btn list-btn btn-warning"
				href="<c:url value='/degree/${degreeId}/competence/${competenceId}/learninggoal/${model.learningGoal.id}/modify.htm'/>">				
				<span class="glyphicon glyphicon-edit" aria-hidden="true">&nbsp;</span>
				Edit</a>
			</sec:authorize>
		</div>

		<div class="panel-body">


			<div class="form-group">
				<div class="form-group view">
					<label>Code: </label> 
					<p class="details">${model.learningGoal.info.code}</p>
				</div>
				<div class="form-group view">
					<label>Name: </label> 
					<p class="details">${model.learningGoal.info.name}</p>
				</div>
				<div class="form-group view">
					<label>Description: </label> 
					<p class="details">${model.learningGoal.info.description}</p>
				</div>
				<div class="form-group view">
					<label>Topic: </label> 
					<p class="details">${model.learningGoal.competence.info.name}</p>
				</div>
			</div>

		</div>
	</div>

	
</body>

</html>
