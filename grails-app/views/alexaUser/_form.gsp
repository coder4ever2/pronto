<%@ page import="omniapi.AlexaUser" %>



<div class="fieldcontain ${hasErrors(bean: alexaUserInstance, field: 'alexaUserId', 'error')} ">
	<label for="alexaUserId">
		<g:message code="alexaUser.alexaUserId.label" default="Alexa User Id" />
		
	</label>
	<g:textField name="alexaUserId" value="${alexaUserInstance?.alexaUserId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: alexaUserInstance, field: 'verificatonCode', 'error')} ">
	<label for="verificatonCode">
		<g:message code="alexaUser.verificatonCode.label" default="Verificaton Code" />
		
	</label>
	<g:textField name="verificatonCode" value="${alexaUserInstance?.verificatonCode}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: alexaUserInstance, field: 'phone', 'error')} ">
	<label for="phone">
		<g:message code="alexaUser.phone.label" default="Phone" />
		
	</label>
	<g:textField name="phone" value="${alexaUserInstance?.phone}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: alexaUserInstance, field: 'isVerified', 'error')} ">
	<label for="isVerified">
		<g:message code="alexaUser.isVerified.label" default="Is Verified" />
		
	</label>
	<g:checkBox name="isVerified" value="${alexaUserInstance?.isVerified}" />

</div>

