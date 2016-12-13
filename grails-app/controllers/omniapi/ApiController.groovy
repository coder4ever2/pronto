package omniapi

import grails.converters.JSON;
import grails.converters.XML
import grails.rest.RestfulController
import groovy.xml.MarkupBuilder

class ApiController extends RestfulController {
	static final String appName = 'Omni'
    static responseFormats = ['json', 'xml']

	
	def apiService

    /*ApiController() {
        super(User)
    }*/

    ApiController(Class resource) {
        super(resource)
    }

	def beforeInterceptor = [action: this.&beforeInterceptor, except: [

			//'inviteFriendToEvent',
			//'postLisnMessageV3',
			//'loginWithPhone'
	]]
	def beforeInterceptor(){
		log.warn "Request with params:"
		log.warn params 
		log.warn "Json :"
		log.warn request.JSON
		
	}
	def afterInterceptor = { model ->
		log.warn "Tracing in afterInterceptor:"
		log.warn actionUri 
		log.warn params
		log.warn "Json :"
		log.warn request.JSON
	}

    def index(def params) {
		def result = [:]
		result['status']='success'
		result['app_token']='97kjhasdf8iojnsdfl'
		render result as JSON;
	}
	
	def signupOnAlexaApp() {
		def result = [:]
		try {
			result = apiService.signupOnAlexaApp(request)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
		
	}
	
	def signup() {
		def result = [:]
		try {
			result = apiService.signup(request)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
		
	}
	def signupWithMultipart() {
		def result = [:]
		try {
			result = apiService.signupWithMultipart(request)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
		
	}
	
	def sendVerificationCode (def to){
		def toPhone = to?to:"+919000520792"
		apiService.sendVerificationCode(toPhone)
	}
	
	def callWithVerificationCode(){
		def result = [:]
		try {
			result = apiService.callWithVerificationCode(request, params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def twilioConf(){
		def result = [:]
		try {
			result = apiService.twilioConf(request, params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def syncContacts(){
		def result = [:]
		try {
			result = apiService.syncContacts(request, params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def callBusiness(){
		def result = [:]
		try {
			result = apiService.callBusiness(request, params)
		} catch (Exception e) {
			handleException(e)
			result.message = 'Error. Please check your number and try again.'
		}
		render result as JSON
	}
	
	def getConfXML(){
		
		def confId ='Conference'+System.currentTimeMillis()
		if(params.confId){
			confId = params.confId
		}
		
		render(contentType: "text/xml", text:"<Response>"+
		"<Dial><Conference>" +confId+"</Conference>"+
		"<Number>5103786712</Number>"+
		"<Number>5102741357</Number>"+
		"</Dial></Response>") 
			
		/*
		<Response>
		<Dial>
			<Conference>MyConference</Conference>
		</Dial>
	</Response>*/
	}
	
	def alexaCallXML(){
		render (contentType: "text/xml", text: """
			<Response>
				<Say voice="alice" loop="2">Please wait, Jay is connecting your call.</Say>
			</Response>
		""")
		
	}

	def businessCallXML(){
		render (contentType: "text/xml", text: """
			<Response>
				<Say voice="alice" loop="2">Please wait, we are connecting a call from your customer.</Say>
			</Response>
		""")
		
	}
	
	def getFirstCallOn(){
		render (contentType:"text/xml", text:"<Response>"
        +"<Dial>"
         +       "<Number statusCallbackEvent=\"answered completed\""
         +        "statusCallback=\"https://lisnx.com/omni/api/callAnswered?phone1="+params.phone1+"&phone2="+params.phone2+"&callId="+params.callId   +"\""
          +       "statusCallbackMethod=\"POST\""
		  	+ ">"
           +             params.phone1
            +    "</Number>"
        +"</Dial>"
         +"</Response>")
	}
	
	def connectSecondCall(){
		def result = [:]
		try {
			apiService.connectSecondCall(request,params)
			result.status = 'success'
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def test(){
		def result
		try {
			result = apiService.test(request,params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def alexa(){
		def result =[:]
		def errorMessage = "Oops, something went wrong. Please try again."
		def defaultResponse = [outputSpeech : [type:'SSML',
			ssml:"""
				<speak>${errorMessage}</speak>
							"""
				]]
		result.response = defaultResponse
		
		try {
			result = apiService.alexa(request,params)
		} catch (Exception e) {
			log.error("Exception:",e)
			result.response = defaultResponse
		}
		render result as JSON
	}
	
	def gatherAndCallNext(){
		render (contentType:"text/xml", text:"<Response>"
			+"<Dial><Number>"+params.phone2+"</Number></Dial>"
			 +"</Response>")
		/*
		 * <Response>
    <Gather action="/process_gather.php" method="GET">
        <Say>
            Please enter your account number,
            followed by the pound sign
        </Say>
    </Gather>
    <Say>We didn't receive any input. Goodbye!</Say>
</Response>
		 */
	}
	
	
	
	
	def connectSecondCallXML(){
		render (contentType:"text/xml", text:"<Response>"
			+"<Dial><Number>"+params.phone2+"</Number></Dial>"
			 +"</Response>")
	}
	
	def callAnswered(){
		def result = [:]
		try {
			apiService.callAnswered(request,params)
			result.status = 'success'
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def getVoiceXML(){
		def writer = new StringWriter()
		def xml = new MarkupBuilder()
		String verificationCode = " 1 2 3 4 5 6"
		render(contentType: "text/xml") {
         Response {
			
			/*Pause(length :'1')
			 
            Say(voice:'alice',
				'Please stay on the line for an important message from omni messenger dot i o .' )*/
			Pause(length :'3')
			
			/*Say(voice:'alice', loop:'2',
				'Thank you for downloading and claiming business on omni messenger app. '
				+ 'To complete your signup, please enter the verification code '+verificationCode +'. '
				+ 'The verification code again is ' +verificationCode +'. '
				+'If you are having trouble registering or claiming, please send an email to info@omnimessenger.io. '
				+'Welcome again and chat with you soon on omni messenger.')
            
            Pause(length:'1')
			Say(voice:'alice', 'Thank you!')*/
			def voiceBaseUrl = "http://lisnx.com/omni/files/signup"
			if(params.female){
				voiceBaseUrl = "http://lisnx.com/omni/files/signupfbg"
			}
			Play(voiceBaseUrl+"1.mp3")
			if(params.code){
				for (int i=0; i<params.code.length(); i++){
					Play(voiceBaseUrl+"3_"+params.code.chars[i] +".mp3")
				}
				
			}
			Play(voiceBaseUrl+"2.mp3")
			
			if(params.code){
				for (int i=0; i<params.code.length(); i++){
					Play(voiceBaseUrl+"3_"+params.code.chars[i] +".mp3")
				}
				
			}
			
			
			Play(voiceBaseUrl+"4.mp3")
			
			
		//	Play("http://lisnx.com/omni/files/welcome_part2.mp3")
			//Say(voice:'alice', verificationCode)
			//Play("http://lisnx.com/omni/files/welcome_part3.mp3")
			
				
				
         	}
		}
		 
		/*def result = '''
			<Response>
			<Say voice="alice">
			Thanks for the registering with OMNI. Please enter the following verification code in the app to complete your registration.
			</Say>
			<Pause length="1"/>
			<Say voice="alice">
			Let us know if we can help you in any way during your development.
			</Say>
			</Response>
		'''
		render new XmlParser().parseText(xml) as XML*/
	}
	
	
	/*
	 * 
	 */
	def resendVerificationCode (){
		def result = [:]
		try {
			apiService.resendVerificationCode(request)
			result.status = 'success'
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def confirmVerificationCode (){
		def results = [:]
		if(apiService.confirmVerificationCode(request.JSON)){
			results.status = 'Success'
		}else
			results.status = 'Failure'
			
		render results as JSON
	}
	
	def searchBusinesses(){
		
		def result = [:]
		try {
			result = apiService.searchBusinesses(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}	
		render result as JSON
	}
	
	def getBusinessDetail(){
		def result = [:]
		try {
			result = apiService.getBusinessDetail(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
		
	}
	
	def requestFeedback(){
		def result = [:]
		try {
			result = apiService.requestFeedback(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
		
	}
	/**
	 * 
	 */
	def getAutocompleteBusinessNames(){
		def result = [:]
		try {
			result = apiService.getAutocompleteBusinessNames(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	/**
	 * @deprecated, see searchBusinesses
	 * @param params
	 * @return
	 */
	def findBusinesses(def params){
		def results = [:]
		results['status']='Success';
		def searchResults = []
		/*def searchResult1 =[:]
		searchResult1.icon="http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png"
		searchResult1.id = "21a0b251c9b8392186142c798263e289fe45b4aa"
		searchResult1.name = "Big Bazaar - Koti"
		searchResult1.address = '4-5-158/4, Women\'s College Road, Koti, Hyderabad'
		searchResult1.email = "email1@bigbazaar.com"
		searchResult1.phone = "+9000678876"
		searchResult1.distance = "1km"
		searchResult1.reviews = '501 reviews'
		searchResult1.opening_hours = [:]
		searchResult1.opening_hours.open_now = true
		searchResult1.opening_hours.message="10-5pm"
			/*},
			"photos" : [
			   {
				  "height" : 270,
				  "html_attributions" : [],
				  "photo_reference" : "CnRnAAAAF-LjFR1ZV93eawe1cU_3QNMCNmaGkowY7CnOf-kcNmPhNnPEG9W979jOuJJ1sGr75rhD5hqKzjD8vbMbSsRnq_Ni3ZIGfY6hKWmsOf3qHKJInkm4h55lzvLAXJVc-Rr4kI9O1tmIblblUpg2oqoq8RIQRMQJhFsTr5s9haxQ07EQHxoUO0ICubVFGYfJiMUPor1GnIWb5i8",
				  "width" : 519
			   }
			],
			"place_id" : "ChIJyWEHuEmuEmsRm9hTkapTCrk",
			"scope" : "GOOGLE",
			"alt_ids" : [
			   {
				  "place_id" : "D9iJyWEHuEmuEmsRm9hTkapTCrk",
				  "scope" : "APP"
			   }
			],
			"reference" : "CoQBdQAAAFSiijw5-cAV68xdf2O18pKIZ0seJh03u9h9wk_lEdG-cP1dWvp_QGS4SNCBMk_fB06YRsfMrNkINtPez22p5lRIlj5ty_HmcNwcl6GZXbD2RdXsVfLYlQwnZQcnu7ihkjZp_2gk1-fWXql3GQ8-1BEGwgCxG-eaSnIJIBPuIpihEhAY1WYdxPvOWsPnb2-nGb6QGhTipN0lgaLpQTnkcMeAIEvCsSa0Ww",
			"types" : [ "travel_agency", "restaurant", "food", "establishment" ],
			"vicinity" : "Pyrmont Bay Wharf Darling Dr, Sydney"
		 }
		def searchResults = []
		searchResults.add(searchResult1)
		def searchResult2 =[:]
		
		searchResult2.icon="http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png"
		searchResult2.id = "21a0b251c9b8392186142c798263e289fe45b4ab"
		searchResult2.name = "Big Bazaar - Hitech city"
		searchResult2.address = "F 48, 1st Floor, Inorbit Mall, Hitech City, Hyderabad"
		searchResult2.email = "email2@bigbazaar.com"
		searchResult2.phone = "+9000908876"
		searchResult2.distance = "5.1km"
		searchResult2.reviews = '10 reviews'
		searchResult2.opening_hours = [:]
		searchResult2.opening_hours.message="10-5pm"
		searchResult2.opening_hours.open_now = true
		searchResults.add(searchResult2)
		
		def searchResult3 =[:]
		
		searchResult3.icon="http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png"
		searchResult3.id = "21a0b251c9b8392186142c798263e289fe45b4ac"
		searchResult3.name = "Big Bazaar - Kukatpally"
		searchResult3.distance = "11.2km"
		searchResult3.email = "email3@bigbazaar.com"
		searchResult3.phone = "+9340678876"
		
		searchResult3.address = "Floor 2, APHB Commercial Complex, Near Shiva Parvati Theatre, Kukatpally, Hyderabad"
		searchResult3.reviews = '1 review'
		
		searchResult3.opening_hours = [:]
		searchResult3.opening_hours.message="10-5pm"
		searchResult3.opening_hours.open_now = true
		searchResults.add(searchResult3)
		*/
		def businesses = Business.all
		businesses.each {business ->
			def searchResult =[:]
			searchResult.icon = business.icon
			searchResult.id = business.id
			searchResult.businessId = business.googleId
			searchResult.name = business.name
			searchResult.distance = '5.1km'
			searchResult.email = "info@"+business.name?.replaceAll(' ','')+'.com'
			searchResult.phone = "+9340678876"
			searchResult.address = business.addressString
			searchResult.reviews = '10 reviews'
			searchResult.opening_hours=[:]
			searchResult.opening_hours.message = business.openHours 
			searchResult.opening_hours.open_now = true
			searchResults.add(searchResult)
		}
		
		results['searchResults']=searchResults// TODO: remove this after changes are done on iOS. 
		results.message = searchResults
		render results as JSON
	}
	

	
	def addChatMessage(){
		def result = [:]
		try {
			result = apiService.addChatMessage(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def createNewChat(){
		def result = [:]
		try {
			result = apiService.createNewChat(request, params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def getNewChatMessages(){
		def result = [:]
		try {
			result = apiService.getNewChatMessages(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	def getAllChatMessages(){
		def result = [:]
		try {
			result = apiService.getAllChatMessages(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	
	def getChatMessages(){
		def result = [:]
		try {
			result = apiService.getChatMessages(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	
	def getChats(){
		def result = [:]
		try {
			result = apiService.getChats(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	def createChat(){
		def result = [:]
		try {
			result = apiService.createChat(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def claimBusinessForSignedUpUser(){
		def result = [:]
		try {
			result = apiService.claimBusinessForSignedUpUser(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def claimBusinessForNewUser(){
		def result = [:]
		try {
			result = apiService.claimBusinessForNewUser(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def addReview(){
		def result = [:]
		try {
			result = apiService.addReview(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def getReviews(){
		def result = [:]
		try {
			result = apiService.getReviews(request, params)
		
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	def getMoreInfo(){
		def result = [:]
		try {
			result = apiService.getMoreInfo(request, params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def testPush(){
		def result = [:]
		try {
			result = apiService.testPush(request, params)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
	}
	
	def createTestBusiness(){
		def result = [:]
		try {
			result = apiService.createTestBusiness(request)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON
		
	}
	
	def handleException(Exception e) {
		log.error("Exception:",e)
		def result = [:]
		result["status"] = "error"
		result.msg =  "An error occured. Please try again"
		result.status_code = 2

		if (e.cause?.message != null && !"null".equals(e.cause?.message)) {
			result["message"] = "Error:" + e.getCause()?.getMessage()
		} else {
			try {
				result["message"] = "Error:" +  e.details?.message
			} catch (Exception ex) {
				result["message"] = "Error:"
			}
		}
	   
		render result as JSON
	}

	def registerDeviceToken() {
		def result = [:]
		try {
			result = apiService.registerDeviceToken(request)
		} catch (Exception e) {
			handleException(e)
		}
		render result as JSON

	}
	
	
}
	
	

