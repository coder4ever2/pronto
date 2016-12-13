
<%@ page import="omniapi.AlexaUser" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'alexaUser.label', default: 'AlexaUser')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-alexaUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-alexaUser" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="alexaUserId" title="${message(code: 'alexaUser.alexaUserId.label', default: 'Alexa User Id')}" />
					
						<g:sortableColumn property="verificatonCode" title="${message(code: 'alexaUser.verificatonCode.label', default: 'Verificaton Code')}" />
					
						<g:sortableColumn property="phone" title="${message(code: 'alexaUser.phone.label', default: 'Phone')}" />
					
						<g:sortableColumn property="isVerified" title="${message(code: 'alexaUser.isVerified.label', default: 'Is Verified')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${alexaUserInstanceList}" status="i" var="alexaUserInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${alexaUserInstance.id}">${fieldValue(bean: alexaUserInstance, field: "alexaUserId")}</g:link></td>
					
						<td>${fieldValue(bean: alexaUserInstance, field: "verificatonCode")}</td>
					
						<td>${fieldValue(bean: alexaUserInstance, field: "phone")}</td>
					
						<td><g:formatBoolean boolean="${alexaUserInstance.isVerified}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${alexaUserInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
