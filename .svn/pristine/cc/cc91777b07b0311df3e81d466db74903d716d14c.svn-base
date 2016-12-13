<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'alexaUser.label', default: 'AlexaUser')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body style="width:500px">
		<a href="#create-alexaUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<!-- <div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>-->
		<div id="create-alexaUser" class="content scaffold-create" role="main">
			<h1>Welcome to Pronto. Enter your phone number.</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${alexaUserInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${alexaUserInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form url="[resource:alexaUserInstance, action:'savePhone']" >
				<fieldset class="form">
					
					<div class="fieldcontain ${hasErrors(bean: alexaUserInstance, field: 'phone', 'error')} ">
						<g:textField name="phone" value="${alexaUserInstance?.phone}"/> &nbsp;&nbsp;
						<g:submitButton name="create" class="save" value="Submit" />
					
					</div>
				</fieldset>
				
			</g:form>
		</div>
	</body>
</html>
