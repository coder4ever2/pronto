<!DOCTYPE html>
<html lang="en">
<head>
	
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0">
  <script>
var x = document.getElementById("demo");
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}
function showPosition(position) {
    x.innerHTML = "Latitude: " + position.coords.latitude + 
    "<br>Longitude: " + position.coords.longitude; 
}
</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-77776615-1', 'auto');
  ga('require', 'linkid');
  ga('send', 'pageview');

</script>
</head>
<body>
<div id="demo"></div>
<fieldset class="form">
    <g:form action="scrape" method="GET">
    	<div class="fieldcontain">
            <label for="query">Latitude</label>
            <g:textField name="latitude" value="${params.latitude}"/>
        </div>
    	<div class="fieldcontain">
            <label for="query">Longitude</label>
            <g:textField name="longitude" value="${params.longitude}"/>
        </div>
        <div class="fieldcontain">
            <label for="query">Keywords</label>
            <g:textField name="query" value="${params.keyword}"/>
        </div>
        
    </g:form>
</fieldset>
<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'business.businessName', default: 'Name')}" />
					
						<g:sortableColumn property="businessPhone" title="${message(code: 'business.businessPhone', default: 'Phone')}" />
					
						<g:sortableColumn property="addressString" title="${message(code: 'business.address', default: 'Address')}" />
					
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${businesses}" status="i" var="business">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="" params="[name:business?.name+'_'+business?.id]">${business?.name}</g:link></td>
					
						<td>${business?.name}</td>
					
						<td>${business?.addressString}</td>
					
						
					
					</tr>
				</g:each>
				</tbody>
			</table>

</body>
</html>