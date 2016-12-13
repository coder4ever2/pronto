<!DOCTYPE html>
<html lang="en">
<head>
	
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0">
  <title>Search, Call, Customer Service, Contact Us Page</title>
  <meta name="description" content="Search and contact businesses - aided by AI bot doing all the redialing and waiting on hold.">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.1/css/font-awesome.min.css">
  <link rel="stylesheet" href="http://lisnx.com/omni/css/style.min.css">
  <link rel="stylesheet" href="http://lisnx.com/omni/css/hover.css">

<style>
.form-control {
   padding: 0.5em;
}
/* Bounce To Right */
.hvr-bounce-to-right {
  display: inline-block;
  vertical-align: middle;
  -webkit-transform: translateZ(0);
  transform: translateZ(0);
  box-shadow: 0 0 1px rgba(0, 0, 0, 0);
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  -moz-osx-font-smoothing: grayscale;
  position: relative;
  -webkit-transition-property: color;
  transition-property: color;
  -webkit-transition-duration: 0.5s;
  transition-duration: 0.5s;
  background-color: #79BD9A;
  text-decoration: none;
  color: white;
  padding: 0.5em;
   -webkit-animation-name: greenPulse;
  -webkit-animation-duration: 2s;
  -webkit-animation-iteration-count: infinite;
 
}



.hvr-bounce-to-right:before {
  content: "";
  position: absolute;
  z-index: -1;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #2098d1;
  -webkit-transform: scaleX(0);
  transform: scaleX(0);
  -webkit-transform-origin: 0 50%;
  transform-origin: 0 50%;
  -webkit-transition-property: transform;
  transition-property: transform;
  -webkit-transition-duration: 0.5s;
  transition-duration: 0.5s;
  -webkit-transition-timing-function: ease-out;
  transition-timing-function: ease-out;
}
.hvr-bounce-to-right:hover, .hvr-bounce-to-right:focus, .hvr-bounce-to-right:active {
  color: white;
}
.hvr-bounce-to-right:hover:before, .hvr-bounce-to-right:focus:before, .hvr-bounce-to-right:active:before {
  -webkit-transform: scaleX(1);
  transform: scaleX(1);
  -webkit-transition-timing-function: cubic-bezier(0.52, 1.64, 0.37, 0.66);
  transition-timing-function: cubic-bezier(0.52, 1.64, 0.37, 0.66);
}
/* Bubble Top */
.hvr-bubble-top {
  display: inline-block;
  vertical-align: middle;
  -webkit-transform: translateZ(0);
  transform: translateZ(0);
  box-shadow: 0 0 1px rgba(0, 0, 0, 0);
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  -moz-osx-font-smoothing: grayscale;
  position: relative;
}
.hvr-bubble-top:before {
  pointer-events: none;
  position: absolute;
  z-index: -1;
  content: '';
  border-style: solid;
  -webkit-transition-duration: 0.3s;
  transition-duration: 0.3s;
  -webkit-transition-property: transform;
  transition-property: transform;
  left: calc(50% - 10px);
  top: 0;
  border-width: 0 10px 10px 10px;
  border-color: transparent transparent #e1e1e1 transparent;
}
.hvr-bubble-top:hover:before, .hvr-bubble-top:focus:before, .hvr-bubble-top:active:before {
  -webkit-transform: translateY(-10px);
  transform: translateY(-10px);
}
.userInfo {
   animation: form-fly-up 0.45s ease;
}
@keyframes form-fly-up {
   0% { transform: translateX(50px); }
   50% { transform: translateX(-50px); }
   100% { transform: translateX(0); }
}
@-webkit-keyframes greenPulse {
  from { background-color: #749a02; }
  50% { background-color: #91bd09; }
  to { background-color: #749a02;  }
}
a.green.button {
  -webkit-animation-name: greenPulse;
  -webkit-animation-duration: 2s;
  -webkit-animation-iteration-count: infinite;
  color: white;
  padding: 0.5em;
}
</style>
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw&libraries=places"></script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-77776615-1', 'auto');
  ga('require', 'linkid');
  ga('send', 'pageview', location.pathname);
 

</script>
<script>
function showUserInfoDiv(){
	$('.userInfo').css({opacity: 1.0, display: "inline"});//.animate({opacity: 1.0});
	$('#userInfoDiv').removeClass("userInfo").addClass("userInfo");
	$('.form-control').focus();
	$('.hvr-bounce-to-right').prop('disabled', true);
    var userPhoneNumber = localStorage.getItem('userPhoneNumber');
    if (userPhoneNumber) {
      $('#numberInput').val(userPhoneNumber);
    }
	
}
</script>

<script src="https://code.jquery.com/jquery-2.2.3.js"   integrity="sha256-laXWtGydpwqJ8JA+X9x2miwmaiKhn8tVmOVEigRNtP4=" crossorigin="anonymous"></script>

</head>
<body>
  
<header class="o-header">
  <nav class="o-header-nav">
    <a href="http://www.pronto.ai/" class="o-header-nav__link"><i class="fa fa-arrow-left"></i> Powered by Pronto.AI</a>
    <a href="http://www.pronto.ai/" class="o-header-nav__link">Pronto.AI<i class="fa fa-star"></i></a>
  </nav>
  <div class="o-container">
    
    	
 		<div class="o-address">
				<g:form action="search" method="GET">
					<div class="fieldcontain" style="padding:200px 0px 20px 0px">
						
						<g:textField name="keyword" value="${params.keyword}" style="text-align:left; width:300px; height:30px; font-size:1em" placeholder="Search.."/>
					</div>
				</g:form>
			</div>
 		<div class ="o-address" style="text-align:center;">
 			<g:each in="${businesses}" var="p">
 				<g:link action="index" id="${p.id}">
 					<li>${p.name}, ${p.addressString}</li>
 				</g:link>
			</g:each>
 		</div>
 		
  </div>
</header>
<!-- 
<main class="o-content" onclick="myFunction()">
  <div class="o-container">
    
    <div class="c-slack">
      <span class="c-slack__dot c-slack__dot--a"></span>
      <p>I am a Bot</p>
      <span class="c-slack__dot c-slack__dot--b"></span>
      <p>that calls for you</p>
      <span class="c-slack__dot c-slack__dot--c"></span>
      <p>Click now.</p>
      <span class="c-slack__dot c-slack__dot--d"></span>
    </div>
     -->

    <div id="github-icons"></div>
  </div>
</main>

<footer class="o-footer">
  <div class="o-container">
    <small>&copy; 2016, www.pronto.ai</small>
  </div>
</footer>



</body>
</html>