
<%@ page import="omniapi.AlexaUser" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'alexaUser.label', default: 'AlexaUser')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-alexaUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-alexaUser" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list alexaUser">
			
				<g:if test="${alexaUserInstance?.alexaUserId}">
				<li class="fieldcontain">
					<span id="alexaUserId-label" class="property-label"><g:message code="alexaUser.alexaUserId.label" default="Alexa User Id" /></span>
					
						<span class="property-value" aria-labelledby="alexaUserId-label"><g:fieldValue bean="${alexaUserInstance}" field="alexaUserId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${alexaUserInstance?.verificatonCode}">
				<li class="fieldcontain">
					<span id="verificatonCode-label" class="property-label"><g:message code="alexaUser.verificatonCode.label" default="Verificaton Code" /></span>
					
						<span class="property-value" aria-labelledby="verificatonCode-label"><g:fieldValue bean="${alexaUserInstance}" field="verificatonCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${alexaUserInstance?.phone}">
				<li class="fieldcontain">
					<span id="phone-label" class="property-label"><g:message code="alexaUser.phone.label" default="Phone" /></span>
					
						<span class="property-value" aria-labelledby="phone-label"><g:fieldValue bean="${alexaUserInstance}" field="phone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${alexaUserInstance?.isVerified}">
				<li class="fieldcontain">
					<span id="isVerified-label" class="property-label"><g:message code="alexaUser.isVerified.label" default="Is Verified" /></span>
					
						<span class="property-value" aria-labelledby="isVerified-label"><g:formatBoolean boolean="${alexaUserInstance?.isVerified}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:alexaUserInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${alexaUserInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
