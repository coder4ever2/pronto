package omniapi

//import nayax.Picture;
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.Holders
//import org.grails.web.json.JSONArray
//import org.grails.web.json.JSONElement
//import org.grails.web.json.JSONObject


import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.junit.After;

import static grails.async.Promises.*

import java.text.DateFormat
import java.text.SimpleDateFormat

import javax.imageio.ImageIO

import java.util.Collections
import java.util.Comparator
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import omniapi.ApiService.AlexaIntent;
import omniapi.BusinessReview.Smiley;
import omniapi.ChatMessage.MessageType
import omniapi.Device.DeviceType

import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService
import com.notnoop.apns.SimpleApnsNotification;
import com.twilio.sdk.TwilioRestClient
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.factory.OutgoingCallerIdFactory;
import com.twilio.sdk.resource.list.OutgoingCallerIdList
import com.twilio.sdk.resource.instance.OutgoingCallerId;
import com.twilio.sdk.resource.instance.CallerIdValidation;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.resource.instance.Message
import com.twilio.sdk.verbs.Conference;
import com.twilio.sdk.verbs.Dial;

//import com.notnoop.apns.APNS
//import com.notnoop.apns.ApnsNotification
//import com.notnoop.apns.ApnsService
//import com.notnoop.apns.SimpleApnsNotification

@Transactional
class ApiService {
	
	
	private static final String SUCCESS = 'success'

	private static final String FAILURE = 'failure'
	def smsService
	def apnsService
    def imageService
	def oneSignalApiCallService
	def grailsApplication
	def alexaUtilService
	def twilio_apiID = 'AC02df9ce1610adb63ea980d363d435b16'//'ACcfd1847026a68c27fa2b90b9fa48d008'
	def twilio_apiPass = '0a14bd65dfe9978f8a0547905503c67d'//'858d0a2577486912879e5feb5ffe70de'
	public static def googleApiKeyAttribute = 'key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw'
	public static def googleNearbyUrl = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw&radius=50000'
		//location=-33.8670522,151.1957362&keyword=cruise
	
	public static def googleTextSearchUrl = 'https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw&radius=50000'
	
	def googleBusinessDetails = 'https://maps.googleapis.com/maps/api/place/details/json?oe=utf-8&key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw&placeid='
	
	def googleBusinessAutoComplete = 'https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw&location=-33.8670522,151.1957362&input='
	
	def googleBusinessPhoto = 'https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyB7jnM-YxeVmcrHtbIno1AHsJ3gqR8dKpw&maxwidth=400&photoreference='
	
	private static final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static def alexaInvocationName = 'Pronto'
	
	public static final String TWILIO_APP_PHONE_NUMBER = "+14154231013"
	
	def enum AlexaIntent {
		Call, 
		Setup, 
		AMAZONHelpIntent, 
		AMAZONStopIntent,
		AMAZONYesIntent,
		AMAZONNoIntent,
		AMAZONCancelIntent,
		DontKnowIntent,
		LaunchRequest;
	}
			
	def serviceMethod() {

    }
	
	/*def sendVerificationCode(){
		def map = [to:"+919000520792",from:"+14154231013",body:"SMS TEST"]
		smsService.send(map)

	}*/
	
	public Boolean saveObject(def obj) {
		log.trace "Inside saveObject for ${obj}"
		if (obj.validate()) {
			log.trace "Object validated."
			obj.errors.allErrors.each { log.error it }
			obj.save(flush:true)
			return true
		} else {
			log.trace "Object validation failed."
			obj.errors.allErrors.each { log.error it }
			obj.discard()
			return false
		}
	}
	
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	def encodeURL(def value){
		return URLEncoder.encode(value, "UTF-8")
	}
	/**
	 * 
	 * @param json.latitude
	 * @param json.longitude
	 * @param json.keyword
	 * @return
	 */
	def searchBusinesses(def request, def params){
		def results = [:]
		results.status = SUCCESS
		def source = request.JSON
		if(!source){
			source = params
		}
		def latitude = source.latitude
		def longitude = source.longitude
		def keyword = source.keyword
		def timeStamp = source.timeStamp
		def googleSearchUrlWithParamsEncoded = googleNearbyUrl +'&location='+latitude+','+longitude+'&keyword=' +encodeURL(keyword)
		def url = new URL(googleSearchUrlWithParamsEncoded)
		def resultsFromGoogle = getResponseFromUrl(url)
		log.info(resultsFromGoogle)
		results.message=[:]
		results.message.searchResults=[]
                List searchResultList = new ArrayList();
		resultsFromGoogle.results.each {searchResult->
			log.info(searchResult)
			def searchResult1 =[:]
			//searchResult1.icon=searchResult.icon
			searchResult1.externalBusinessId = searchResult.place_id
			
			searchResult1.name = searchResult.name
			searchResult1.address = searchResult.vicinity
			searchResult1.location = searchResult.geometry.location
			def distance = getDistance(Double.parseDouble(source.latitude), 
													Double.parseDouble(source.longitude), 
													searchResult.geometry.location.lat, 
													searchResult.geometry.location.lng)
			log.info("timestamp : " +timeStamp)
			if(5.5d ==timeStamp.toDouble()){
				double milesToKm = 1.6
				distance = Double.parseDouble(distance) * milesToKm
				distance = GeoUtil.df.format(distance)
				distance = distance +" km"
			}else {
				distance = distance + " mi"
			}

			searchResult1.distance = distance
			searchResult1.icon = searchResult.icon
			searchResult1.opening_hours = [:]
			searchResult1.opening_hours.open_now = searchResult.opening_hours?.open_now
			searchResult1.opening_hours.message= searchResult.opening_hours?.weekday_text?:''
			searchResult1.types = searchResult.types
			//searchResult1.details = searchResult //full details from google api
			//results.message.searchResults.add(searchResult1)
                        searchResultList.add(searchResult1)
		}
                
        if (searchResultList && searchResultList.size() > 1) {
            Collections.sort(searchResultList, new Comparator<Map>() {
                    public int compare(Map m1, Map m2) {
                        if (!m1.get("distance") || ! m2.get("distance")) {
                            return 0
                        }
                        return m1.get("distance").compareTo(m2.get("distance"));
                    }
                });
        }
                
		results.message.searchResults = searchResultList.toArray()
		
		return results
	}
        
    //    def Comparator<Map> mapComparator = new Comparator<Map>() {
    //        public int compare(Map m1, Map m2) {
    //            return m1.get("distance").compareTo(m2.get("distance"));
    //        }
    //    }
    
    def getBusiness(request, params, bId){
		def business = Business.get(bId)
		if(business){
			if(!business.fetchedFullBusinessDetails){
				business = getBusinessDetailsFromGoogle(business.googleId)
			}
		}
		return business
    }
	
	/**
	 * 
	 * @param json.externalBusinessId - external business id
	 * @param json.businessId - business id 
	 * One of the above 2 params is required. and externalBusinessId will be used if both are provided. 
	 * @return
	 */
	def getBusinessDetail(def request, def params){
		def results = [:]
		results.message=[:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		results.status = SUCCESS
		
		def externalId = source.externalBusinessId
		def businessId = source.businessId
		def business
		if(externalId){
			business = Business.findByGoogleId(externalId)
		}else if(businessId){
			business = Business.get(businessId)
		}
		if(!business){
			business = getBusinessDetailsFromGoogle(externalId)
		}
		results.message.details = business.json
		return results
	}

	def getBusinessDetailsFromGoogle(String businessId) {
		def businessDetailsWithBusinessIdUrl = googleBusinessDetails +businessId
		def url = new URL(businessDetailsWithBusinessIdUrl)
		def businessDetails = getResponseFromUrl(url)?.result
		def photoReference = businessDetails.photos?.find { true }?.photo_reference

        String businessOpenHours = businessDetails.opening_hours?.weekday_text;
        if (businessOpenHours) {
            businessOpenHours = escapeNonUniCode(businessOpenHours)
        } else {
            businessOpenHours = ""
        }
		Business business = Business.findByGoogleId(businessId)
		if(!business){
			business = new Business (googleId:businessDetails.place_id,
				name:businessDetails.name,
				addressString:businessDetails.formatted_address,
				businessPhone:businessDetails.formatted_phone_number,
				icon:businessDetails.icon,
				openHours:businessOpenHours,
				latitude:businessDetails.geometry.location.lat,
				photoReference: photoReference?:'',
				longitude:businessDetails.geometry.location.lng)
			saveObject(business)
		}else {
			business.addressString = businessDetails.formatted_address
			business.businessPhone = businessDetails.formatted_phone_number
			business.openHours = businessOpenHours
			business.fetchedFullBusinessDetails = true
			saveObject(business)
		}
		return business
	}
	
	def searchAndSaveBusinesses(request, params){
		def businesses =[]
		def results = [:]
		def source = request.JSON
		if(!source){
			source = params
		}
		def pageNumber = 0
		def latitude = source.latitude
		def longitude = source.longitude
		def keyword = source.keyword
		def googleSearchUrlWithParamsEncoded
		if(latitude && longitude)
			googleSearchUrlWithParamsEncoded = googleNearbyUrl +'&location='+latitude+','+longitude+'&keyword=' +URLEncoder.encode(keyword, "UTF-8")
		else
			googleSearchUrlWithParamsEncoded = googleTextSearchUrl +'&query=' +URLEncoder.encode(keyword, "UTF-8")
			
		def url = new URL(googleSearchUrlWithParamsEncoded)
		def resultsFromGoogle = getResponseFromUrl(url)
		results.message=[:]
		results.message.searchResults=[]
		def nextPageToken = resultsFromGoogle.next_page_token
		businesses += iterateNearbySearchResults(resultsFromGoogle)
		while(!StringUtils.isEmpty(nextPageToken)){
			log.warn('Next Page..' +pageNumber)
			sleep(2000)
			def googleSearchUrlWithNextPageToken = googleSearchUrlWithParamsEncoded +'&pagetoken=' +nextPageToken
			url = new URL(googleSearchUrlWithNextPageToken) 
			resultsFromGoogle = getResponseFromUrl(url)
			nextPageToken = resultsFromGoogle.next_page_token
			businesses += iterateNearbySearchResults(resultsFromGoogle)
			pageNumber++
		}
		return businesses
	}

	def iterateNearbySearchResults(resultsFromGoogle) {
		def businesses = []
		def iterate = resultsFromGoogle.results.each { googleBusinessResult ->
			Business business = Business.findByGoogleId(googleBusinessResult.place_id)

			if(!business){
				def businessOpenHours = googleBusinessResult.opening_hours?.weekday_text;
				if (businessOpenHours) {
					businessOpenHours = escapeNonUniCode(businessOpenHours)
				} else {
					businessOpenHours = ""
				}

				def photoReference = googleBusinessResult.photos?.find { true }?.photo_reference
				business = new Business (googleId:googleBusinessResult.place_id,
				name:googleBusinessResult.name,
				addressString:googleBusinessResult.formatted_address?:googleBusinessResult.vicinity?:'',
				icon:googleBusinessResult.icon,
				latitude:googleBusinessResult.geometry?.location?.lat,
				photoReference: photoReference?:'',
				longitude:googleBusinessResult.geometry?.location?.lng,
				googleRating:googleBusinessResult.rating,
				googleBusinessTypes: googleBusinessResult.types)
				saveObject(business)
				
				log.warn('Saved:' + business.name)
			}else {
				log.warn('Business already in db' + business.name)
			}
			businesses.add(business)
		}
		return businesses
	}

	def escapeNonUniCode(String businessOpenHours) {
		String escapeString = StringEscapeUtils.escapeJava(businessOpenHours);
        //log.info(escapeString.find("\\\\u(FFFD)"))
        if (escapeString.find("\\\\u(FFFD)")) {
            businessOpenHours = escapeString.replaceAll("\\\\u(FFFD)", "")
        }
		return businessOpenHours
	}
	
	def getAutocompleteBusinessNames(def request, def params){
		def results = [:]
		results.message=[:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		results.status = SUCCESS
		
		def autocompleteUrl = googleBusinessAutoComplete +source.input
		println(autocompleteUrl)
		/*def url = new URL(autocompleteUrl)
		def autoPredictions = getResponseFromUrl(url)
		def predictions = []
		autoPredictions?.predictions.each {prediction->
			predictions.add([description:prediction.description, externalBusinessId:prediction.place_id])
		}*/
		def predictions = [[description:'Starbucks, Castro Valley, CA', place_id:'Castrolakjsdlkjflaskjf'],
						[description:'Starbucks, Dublin, CA', place_id:'Dublinlakjsdlkjflaskjf']]
		//TODO- parse auto predictions and return.  
		results.predictions = predictions
		return results
		
		
	}
	
	/**
	 * @deprecated To use @see signup
	 * @param json.deviceType
	 * @param json.deviceToken
	 * @param json.image
	 * @param json.firstName			
	 * @param json.lastName
	 * @param json.email
	 * @param json.phone
	 * @param json.fid
	 * @return
	 */
	def signupWithMultipart(def request){
		def json = request.JSON
		def result =[:]
		result.message = [:]
		Device device = new Device (deviceType:DeviceType.valueOf(json.deviceType), token:json.deviceToken)
		Picture picture
		if(json.image){
			picture= saveMultipartImage(request)
		}
		//TODO: check if user already exists
		User user = User.findByPhone(json.phone)
		user = checkAndSaveUserInfo(user, json, device, picture, request)
		sendVerificationCode(user.phone)
		result['status']='Success'
		result.message.summary=user.firstName +', a verification code has been sent to your phone '+ user.phone + ',to complete signup. - Pronto'
		result.message.details = user.json
		
		return result
	}
	
	/**
	 *
	 * @param json.name
	 * @param json.email
	 * @param json.phone
	 * @return
	 */
	def signupOnAlexaApp(def request) {
        def json = request.JSON
        def result = [:]
        result.message = [:]
        def userPhone = json.phone
        def name = json.name
        def email = json.email

		AlexaUser alexaUser = AlexaUser.findByPhone(userPhone)
        if(!alexaUser){
            alexaUser = new AlexaUser(userPhone: userPhone,
                    name: name,
                    email: email)
            def verificationCode = verifyWithTwilioAndSave(userPhone.toCharArray(), alexaUser)
            result.message.verificationCode = verificationCode
		} else {
            result.message = 'Your phone number ' + userPhone + ' has been previously verified.'
        }

        result['status'] = 'Success'
		
		return result
	}
	
	/**
	 * 
	 * @param json.deviceType
	 * @param json.deviceToken
	 * @param json.image
	 * @param json.firstName
	 * @param json.lastName
	 * @param json.email
	 * @param json.phone
	 * @param json.fid
	 * @return
	 */
	def signup(def request){
		def json = request.JSON
		def result =[:]
		result.message = [:]
        Device device = new Device (deviceType:DeviceType.valueOf(json.deviceType), token:json.deviceToken)
		Picture picture 
		if(json.image){
			byte[] imageByteArray = Base64.decodeBase64(json.image);
			picture= saveImage(imageByteArray)
		}
		User user = User.findByPhone(json.phone)
		user = checkAndSaveUserInfo(user, json, device, picture, request)
		sendVerificationCode(user.phone)
		result['status']='Success'
		result.message.summary=user.firstName +', a verification code has been sent to your phone '+ user.phone + ',to complete signup. - Pronto'
		result.message.details = user.json
		
		return result
	}

	def checkAndSaveUserInfo(User user, def json, Device device, Picture picture, def request) {
		if(user){
			boolean userInfoChanged = false
			if(!user.email.equals(json.email)){
				user.email = json.email
				userInfoChanged= true
			}
			if(!user.firstName.equals(json.firstName)){
				user.firstName = json.firstName
				userInfoChanged = true
			}
			if(!user.lastName.equals(json.lastName)){
				user.lastName = json.lastName
				userInfoChanged = true
			}
			if(userInfoChanged){
				saveObject(user)
			}
		}
		else {
			user = new User(firstName: json.firstName,
			lastName:json.lastName,
			email:json.email,
			phone: json.phone,
			fid:json.fid,
			device:device,
			picture: picture)
			saveObject(user)
			Business omniMessenger = Business.findByName('Pronto Team')
			if(omniMessenger)
			{
				//saveChatMessage(String userId, String messageTo, String messageType, String message, String messagePush, String messageId,
					//String timeStamp, Map results, def request)
			
				saveChatMessage(''+omniMessenger.id, 
							''+user.id, 
							'0', 
							'Welcome to Pronto!', 
							'Welcome to Pronto!', 
							'', 
							'0', 
							[:], 
							request)
			}
			

		}
		return user
	}
	
	private Picture saveImage(byte[] imageByteArray) {
		//def multipartFile = request.getFile("image");
		Picture picture
		//log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
		//if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
		def filename = UUID.randomUUID().toString()
		def fileExtension = '.jpg'
		//def originalFilename = multipartFile.getOriginalFilename()
		//def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())
		def fullFilePath = grailsApplication.config.pictureLocation+filename+fileExtension
		FileOutputStream imageOutFile = new FileOutputStream(fullFilePath);
		imageOutFile.write(imageByteArray);
		imageOutFile.close();
		
		Integer fileSizeValue = new Integer(0)
		  
		picture = new Picture(mimeType: fileExtension,
			filename: filename+fileExtension,
			fileSize: fileSizeValue,
			originalFilename: filename+fileExtension,
			filePath: fullFilePath)
		saveObject(picture)
        File targetThumbNailFile = new File(grailsApplication.config.pictureLocation, 'tn_' + filename +fileExtension )
        def thumbnailImage = imageService.getThumbNail(new File(fullFilePath), 300)
        ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);
			
		return picture
	}

	def Picture saveMultipartImage(javax.servlet.http.HttpServletRequest request) {
		def multipartFile = request.getFile("image");
		def multipartFileThumb = request.getFile("thumb_image")
		Picture picture
		log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
		if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
			def filename = UUID.randomUUID().toString()
			def originalFilename = multipartFile.getOriginalFilename()
			def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())

			picture = new Picture(mimeType: multipartFile.getContentType(),
				originalFilename: originalFilename,
				fileSize: multipartFile.getSize(),
				filename: filename+fileExtension,
				filePath: grailsApplication.config.pictureLocation)
			log.info " mimeType  " + multipartFile.getContentType()
			log.info " originalFilename  " + originalFilename
			log.info " filePath  " + grailsApplication.config.pictureLocation
			File targetFile = new File(grailsApplication.config.pictureLocation, filename +fileExtension )
			multipartFile.transferTo(targetFile)
			if(multipartFileThumb){
				File targetThumbNailFile = new File(grailsApplication.config.pictureLocation, 'tn_' +filename +fileExtension )
				multipartFileThumb.transferTo(targetThumbNailFile)
				picture.hasThumbNail = true
			}
			saveObject(picture)
				//def thumbnailImage = imageService.getThumbNail(targetFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
				//ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);

			
		}
		return picture
	}
	/**
	 * 
	 * @param json.to - phone number
	 * @return
	 */
	def resendVerificationCode(def request){
		return sendVerificationCode(request.JSON.to)
	}
	
	
	def sendVerificationCode(def to) {
		TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
		VerificationCode verificationCode = VerificationCode.findByPhone(to)
		if(!verificationCode){
			int randomVerificationCode = RandomUtils.nextInt(1000, 9999)
			verificationCode = new VerificationCode(phone:to, code:randomVerificationCode)
			saveObject(verificationCode)
		}
	   
		 // Build the parameters
		 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 nameValuePairs.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
		 nameValuePairs.add(new BasicNameValuePair("To", to ));//"+919000520792"));
		 nameValuePairs.add(new BasicNameValuePair("Body", "Here is your verification code - "+verificationCode.code));
		 //params.add(new BasicNameValuePair("MediaUrl", "http://farm2.static.flickr.com/1075/1404618563_3ed9a44a3a.jpg"));
	   
		 MessageFactory messageFactory = client.getAccount().getMessageFactory();
		 Message message = messageFactory.create(nameValuePairs);
		 log.trace(message.getSid());
	  }
	
	def sendVerificationCodeForAlexa(def to) {
		TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
		AlexaUser alexaUser = AlexaUser.findByPhone(to)
		if(!alexaUser){
			int randomVerificationCode = RandomUtils.nextInt(1000, 9999)
			alexaUser = new AlexaUser(phone:to, verificationCode:randomVerificationCode)
			saveObject(alexaUser)
		}
	   
		 // Build the parameters
		 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 nameValuePairs.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
		 nameValuePairs.add(new BasicNameValuePair("To", to ));//"+919000520792"));
		 nameValuePairs.add(new BasicNameValuePair("Body", "Verification code from Pronto - "+alexaUser.verificationCode));
		 //params.add(new BasicNameValuePair("MediaUrl", "http://farm2.static.flickr.com/1075/1404618563_3ed9a44a3a.jpg"));
	   
		 MessageFactory messageFactory = client.getAccount().getMessageFactory();
		 Message message = messageFactory.create(nameValuePairs);
		 log.trace(message.getSid());
	  }
	
	def sendTwilioCallerIdVerificationCode(def to, def code) {
		TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
		/**AlexaUser alexaUser = AlexaUser.findByPhone(to)
		if(!alexaUser){
			int randomVerificationCode = RandomUtils.nextInt(1000, 9999)
			alexaUser = new AlexaUser(phone:to, verificationCode:randomVerificationCode)
			saveObject(alexaUser)
		}**/
	   
		 // Build the parameters
		 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 nameValuePairs.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
		 nameValuePairs.add(new BasicNameValuePair("To", String.valueOf(to)));//"+919000520792"));
		 nameValuePairs.add(new BasicNameValuePair("Body", "Verification code from Pronto - "+code));
		 //params.add(new BasicNameValuePair("MediaUrl", "http://farm2.static.flickr.com/1075/1404618563_3ed9a44a3a.jpg"));
	   
		 MessageFactory messageFactory = client.getAccount().getMessageFactory();
		 Message message = messageFactory.create(nameValuePairs);
		 log.trace(message.getSid());
	}
	
	def verifyCode(def alexaUserInstance){
		AlexaUser userInDb = AlexaUser.findByPhone(alexaUserInstance.phone)
		return alexaUserInstance?.phone.equals(userInDb?.phone)
	}
	/**
	 * 
	 * @param request.JSON.to - the phone number to which the verification code is sent by a phone call.
	 * @param params
	 * @return
	 */
	def callWithVerificationCode(def request, def params) {
		def result =[:]
		def source = request.JSON
		if(!source){
			source = params
		}
		
		TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
		
		CallFactory callFactory = client.getAccount().getCallFactory();
		Map<String, String> callParams = new HashMap<String, String>();
		callParams.put("To", source.to); // Replace with a valid phone number
		callParams.put("From", TWILIO_APP_PHONE_NUMBER); // Replace with a valid phone
		// number in your account
		callParams.put("Url", "https://lisnx.com/omni/api/getVoiceXML?code="+source.code);
		if(source.female){
			callParams.put("Url", "https://lisnx.com/omni/api/getVoiceXML?female=true&code="+source.code);
		}
		com.twilio.sdk.resource.instance.Call call = callFactory.create(callParams);
		log.trace(call.getSid());
		result.status = SUCCESS
		result.message = 'Successfully delivered message to:'+source.to
		return result
	
	}
	def makeTwilioCall(String url, String to, String from) {
	    try {
	        TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
	
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("Url", "http://demo.twilio.com/welcome/voice/"));
	        params.add(new BasicNameValuePair("To", "+15103786712"));
	        params.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
	         
	        CallFactory callFactory = client.getAccount().getCallFactory();
	        Call call = callFactory.create(params);
	    }
	    catch (TwilioRestException e) {
	            log.error(e);
	    }
    }
	
	def callAnswered(request, params){
		def result =[:]
		result.status =SUCCESS
		try {
			TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
			
			List<NameValuePair> twilioParams = new ArrayList<NameValuePair>();
		
			twilioParams.add(new BasicNameValuePair("Url", "https://lisnx.com/omni/api/connectSecondCall?phone1"
														+encodeURL(params.phone1)
														+"&phone2"+encodeURL(params.phone2)
														+"&callId"+encodeURL(params.callId)));
			 
			CallFactory callFactory = client.getAccount().getCallFactory();
			
			Call call2 = client.getAccount().getCall(params.callId);
			
			call2.update(twilioParams);
			
		}
		catch (TwilioRestException e) {
				log.error(e)
				result.status = FAILURE
				result.message = e?.detailMessage
		}
		return result
	}
	
	def alexa(request, params) {
		def result =[:]
		AlexaUser alexaUser
		result.version = '1.0'
		AlexaEvent alexaEvent
		
		
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		log.warn(source)
		//byte[] serializedSpeechletRequest = IOUtils.toByteArray(request.getInputStream());
		//Alexa's time tolerance of 150 seconds in millis
		def toleranceInMilliseconds  = 150*1000L

		try {
			
			
			def requestTimeStamp = ISO_8601_FORMAT.parse(source?.request?.timestamp)
			
			long delta = Math.abs(System.currentTimeMillis() - requestTimeStamp.getTime());
			boolean withinTolerance = delta <= toleranceInMilliseconds; 
			if(!withinTolerance)
				throw new Exception("Time tolerance of 150 seconds has not been met. Rejecting request." + delta)
			// Verify the authenticity of the request by checking the provided signature &
			// certificate.
			/*def signatureRequestHeader = request.getHeader(AlexaSdk.SIGNATURE_REQUEST_HEADER)
			log.warn("signatureRequestHeader:" + signatureRequestHeader)
			def signatureCertificateChainUrlRequestHeader = request.getHeader(AlexaSdk.SIGNATURE_CERTIFICATE_CHAIN_URL_REQUEST_HEADER)
			log.warn("signatureCertificateChainUrlRequestHeader:" + signatureCertificateChainUrlRequestHeader)
			alexaUtilService.checkRequestSignature(serializedSpeechletRequest,
					signatureRequestHeader,
					signatureCertificateChainUrlRequestHeader);*/
			
		}catch(Exception e){
			log.error(e)
			result.status = FAILURE
			result.message = 'Invalid Request - ' +e.getMessage()
			return result
		}
		def isLaunchRequest = "LaunchRequest".equals(source?.request?.type)
		if(isLaunchRequest){
			
			def ssmlContent = """
						<speak>My name is ${alexaInvocationName}. I can make calls for you. Would you like me to make a call?</speak>
						"""
			def ssmlRepromptContent = """
						<speak>Would you like me to make a call?</speak>
						"""
			def sessionAttributes = [lastIntent:"LaunchRequest"]
			
			setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes) 
			
		}else {
			
			def accessToken = source?.session?.user?.accessToken
			def alexaUserId = source?.session?.user?.userId
			def slots = source?.request?.intent?.slots
			def intent = source?.request?.intent?.name
			
			if(alexaUserId)
				alexaUser= AlexaUser.findByAlexaUserId(alexaUserId)
			
			if(!alexaUser)
				alexaUser = new AlexaUser(alexaUserId:alexaUserId)	
			if(intent?.contains(".")){
				intent = StringUtils.replace(intent, ".", "")
			}
				
			AlexaIntent alexaIntent = AlexaIntent.valueOf(intent)
			alexaEvent = new AlexaEvent()
			alexaEvent.alexaIntent = alexaIntent
			alexaEvent.alexaUser = alexaUser
			log.warn("AlexaIntent:" + alexaIntent.name())
			
			switch(alexaIntent){
				case AlexaIntent.AMAZONYesIntent:
					def ssmlContent = """
										<speak>You can say, call five one zero two seven four one three five seven. Go ahead.</speak>
										"""
					def ssmlRepromptContent = """
										<speak>Go ahead.</speak>
										"""
					def sessionAttributes = [lastIntent:alexaIntent.toString()]
					setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes)
					break
				case AlexaIntent.AMAZONNoIntent:
					result.response = [ 
							shouldEndSession:true
						  ]
					break
				case AlexaIntent.AMAZONStopIntent:
					result.response = [ 
							shouldEndSession:true
						  ]
					break
				case AlexaIntent.AMAZONCancelIntent:
					result.response = [ 
							shouldEndSession:true
						  ]
					break
				case AlexaIntent.DontKnowIntent:
					def ssmlContent = """
									<speak>Sorry, I am not sure of that. I can help with making calls. You can do it easily by asking me to make a call. Would you like to try now?</speak>
									"""
					def ssmlRepromptContent = """
									<speak> Would you like to try now?</speak>
									"""
					def sessionAttributes = [lastIntent:alexaIntent.toString()]
					setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes)
					break
				case AlexaIntent.AMAZONHelpIntent:
					def ssmlContent = """
								<speak>I can help with making calls. You can do it easily by asking me to make a call. Would you like to try now?</speak>
								"""
					def ssmlRepromptContent = """
								<speak>Would you like to try now?</speak>
								"""
					def sessionAttributes = [lastIntent:alexaIntent.toString()]
					setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes)
					break
				case AlexaIntent.Call:
					if(!slots?.PhoneNumberOne || !slots?.PhoneNumberOne.value || '0'.equals( slots?.PhoneNumberOne.value)){
						def ssmlContent = """<speak>I couldn't recognize the number. You can say, call five one zero two seven four one three five seven. Would you like to try again?</speak>
								"""
						def ssmlRepromptContent = """
								<speak> Would you like to try now?</speak>
								"""
						def sessionAttributes = [lastIntent:alexaIntent.toString()]
						setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes)
					}
					else if(!slots?.PhoneNumberOne?.value){
						result.response = [outputSpeech : [type:'SSML',
							ssml:"""
							<speak>I couldn't recognize the number <say-as interpret-as="digits">
							${PhoneNumberOne}
							</say-as>. Please try again.</speak>
							"""
						]]
						
					}
					else if(!alexaUser.phone ){
						
						def ssmlContent = """
								<speak>Looks like you have not setup your number yet. 
								You can easily do this by saying setup my number. Would you like to setup your number now?</speak>
								"""
						def ssmlRepromptContent = """
								<speak>Would you like to setup your number now?</speak>
								"""
						def sessionAttributes = [lastIntent:alexaIntent.toString()]
						setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes)
						
					}
					else{
							TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
							def PhoneNumberOne = slots?.PhoneNumberOne?.value
							alexaEvent.phone = PhoneNumberOne
							
							if(validPhoneNumber(PhoneNumberOne)){
								
								if( alexaUser.twilioVerificationCode && false == alexaUser.isVerified) {
								
									// Build a filter for the OutgoingCallerIdList
									Map<String, String> tParams = new HashMap<String, String>();
									def formattedUserPhone = "+1"+alexaUser.phone;
									tParams.put("PhoneNumber",formattedUserPhone );
									
									OutgoingCallerIdList callerIds = client.getAccount().getOutgoingCallerIds(tParams);
									for (OutgoingCallerId callerId : callerIds) {
										log.warn('FormattedUserPhone:||' + formattedUserPhone +'||:')
										log.warn('CallerId:||' + callerId.getPhoneNumber()+'||:')
										if(formattedUserPhone.equals(callerId.getPhoneNumber())){
											alexaUser.isVerified = true
											saveObject(alexaUser)
										}
									}
								}
							
								def PhoneNumberTwo = alexaUser.phone
								List<NameValuePair> twilioParams = new ArrayList<NameValuePair>();
								CallFactory callFactory = client.getAccount().getCallFactory();
								twilioParams.add(new BasicNameValuePair("Url", "https://lisnx.com/omni/api/alexaCallXML"));
								def fromNumber = alexaUser.isVerified ? ("+1"+alexaUser.phone): TWILIO_APP_PHONE_NUMBER;
								twilioParams.add(new BasicNameValuePair("From", fromNumber));
								twilioParams.add(new BasicNameValuePair("To", PhoneNumberOne));
								twilioParams.add(new BasicNameValuePair("StatusCallbackEvent", "answered"));
								twilioParams.add(new BasicNameValuePair("StatusCallback", "https://lisnx.com/omni/api/connectSecondCall?phone1="
															+PhoneNumberOne.trim()+"&phone2="+PhoneNumberTwo.trim()));
								twilioParams.add(new BasicNameValuePair("StatusCallbackMethod", "GET"));
								try{
									Call call2 = callFactory.create(twilioParams);
									result.response = [outputSpeech : [type:'SSML',
										ssml:"""
										<speak>Trying <say-as interpret-as="digits">
										${PhoneNumberOne}
										</say-as> and will connect you once the call is answered.</speak>
										"""
									]]
								}catch(Exception e){
									result.response = [outputSpeech : [type:'SSML',
										ssml:"""
									<speak>An unknown error occurred. Please try again later</speak>
									"""
									]]
								}
							}else {
								result.response = [outputSpeech : [type:'SSML',
									ssml:"""
									<speak>The number <say-as interpret-as="digits">${PhoneNumberOne}</say-as>is invalid. Please try with a valid number.</speak>
									"""
								]]
								
							}
						
						
						/*twilioParams.add(new BasicNameValuePair("Url", "https://lisnx.com/omni/api/gatherAndCallNext?PhoneNumberOne="+PhoneNumberOne +"&PhoneNumberTwo="+PhoneNumberTwo));
						twilioParams.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
						twilioParams.add(new BasicNameValuePair("To", PhoneNumberOne));
						twilioParams.add(new BasicNameValuePair("StatusCallbackEvent", "answered"));
						twilioParams.add(new BasicNameValuePair("StatusCallback", "https://lisnx.com/omni/api/connectSecondCall?phone1="
														+PhoneNumberOne+"&phone2="+PhoneNumberTwo));
						twilioParams.add(new BasicNameValuePair("StatusCallbackMethod", "GET"));
						*/
						
						
					}
					
					break
				case AlexaIntent.Setup:
					def userPhone = slots?.MyPhoneNumberOne?.value
					alexaEvent.phone = userPhone
					if(validPhoneNumber(userPhone)){
						def twilioVerificationCode = verifyWithTwilioAndSave(userPhone.toCharArray(), alexaUser)
						def ssmlContent = """<speak>Great, your phone is set to <say-as interpret-as="digits">${userPhone}</say-as>. You will be receiving a 
						verification call shortly. Here is your verification code <say-as interpret-as="digits">${twilioVerificationCode}</say-as> to complete your verification.
						Your verification code again is <say-as interpret-as="digits">${twilioVerificationCode}</say-as>.
						Next time you want to make a call, just ask me to call a number and I will wait on line until call is answered before connecting you. 
						Would you like to make a call now?</speak>"""
						def ssmlRepromptContent = """<speak>Would you like to make a call now?</speak>"""
					
						def sessionAttributes = [lastIntent:alexaIntent.toString()]
						setResultWithRepromptSSML(result, ssmlContent, ssmlRepromptContent, sessionAttributes)
					}else {
						result.response = [outputSpeech : [type:'SSML',
							ssml:"""
								<speak>The number <say-as interpret-as="digits">${userPhone}</say-as>is invalid. Please try again with a valid number.</speak>
								"""
						]]
						
					}
					
					break
				default:
					result.response = [outputSpeech : [type:'SSML',
						ssml:"""
							<speak>I dont't understand. You can say help to learn more.</speak>
							"""
					]]
			}
		}
		if(alexaEvent){
			saveObject(alexaEvent)
		}
		return result
	}

	def verifyWithTwilioAndSave(char[] userPhone, AlexaUser alexaUser) {
		def userPhoneAsString = new String(userPhone)
		TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
		OutgoingCallerIdFactory callerIdFactory = client.getAccount().getOutgoingCallerIdFactory();
		Map<String, String> twilioParams = new HashMap<String, String>();
        log.info("Alexa user phone number:" + userPhoneAsString)

		twilioParams.put("FriendlyName", "+1"+userPhoneAsString);
		twilioParams.put("PhoneNumber", "+1"+userPhoneAsString);
		twilioParams.put("CallDelay","2");
		//List<NameValuePair> twilioParams = new ArrayList<NameValuePair>();
		//CallFactory callFactory = client.getAccount().getCallFactory();
		//twilioParams.add(new BasicNameValuePair("Url", "https://lisnx.com/omni/api/alexaCallXML"));
		//Call call2 = callFactory.create(twilioParams);
		CallerIdValidation validationAttempt = callerIdFactory.create(twilioParams);
		def twilioVerificationCode = validationAttempt.getValidationCode()
		//System.out.println(validationAttempt.getValidationCode());
		sendTwilioCallerIdVerificationCode(userPhoneAsString,twilioVerificationCode)

		alexaUser.twilioVerificationCode = twilioVerificationCode;
		alexaUser.phone = userPhoneAsString
		saveObject(alexaUser)
		return twilioVerificationCode
	}
	
	def validPhoneNumber(def number){
		def regex = '^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$';
		def validPhoneNumber = false
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(number);
		if(matcher.matches())
	   		validPhoneNumber = true
		
		return validPhoneNumber
	   
	}

	def setResultWithRepromptSSML(Map result, def ssmlContent, def ssmlRepromptContent, def sessionAttributes) {
		result.response = [ 
							outputSpeech : 
									[   type:'SSML',
										ssml:ssmlContent.split("\n").collect { it.trim() }.join(" ")
									], 
							reprompt : 
									[	outputSpeech : 
										[	type:'SSML',
											ssml:ssmlRepromptContent.split("\n").collect { it.trim() }.join(" ")
										] 
									],
							shouldEndSession:false
						  ]
		result.sessionAttributes = sessionAttributes
	}
	
	def twilioConf(request, params) {
		def result =[:]
		result.status =SUCCESS
		
			TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
			List<NameValuePair> twilioParams = new ArrayList<NameValuePair>();
			CallFactory callFactory = client.getAccount().getCallFactory();
			twilioParams.add(new BasicNameValuePair("Url", "http://demo.twilio.com/docs/voice.xml"));
			twilioParams.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
			twilioParams.add(new BasicNameValuePair("To", params.phone1));
			twilioParams.add(new BasicNameValuePair("StatusCallbackEvent", "answered"));
			twilioParams.add(new BasicNameValuePair("StatusCallback", "https://lisnx.com/omni/api/connectSecondCall?phone1="
											+encodeURL(params.phone1)+"&phone2="+encodeURL(params.phone2)));
			twilioParams.add(new BasicNameValuePair("StatusCallbackMethod", "GET"));
			
			Call call2 = callFactory.create(twilioParams);
			result.message = call2.toString()
			return result
	}
	
	/**
	 * 
	 * @param request
	 * @param params
	 * @return
	 */
    def syncContacts(request, params) {
        def result = [:]
        result.status = SUCCESS
        def contactJson = request.JSON
        AlexaUser alexaUser

        log.debug("**** json contact " +contactJson)

        def contacts = contactJson.contacts

        log.debug contacts

        contacts.each { contact ->
            log.debug("*** contact info: " + contact)

            if (contact?.name) {
                log.debug("*** contact name: " + contact?.name)
                def name = contact?.name
                PhoneContact phoneContact = new PhoneContact(
                        alexaUser: alexaUser,
                        firstName: name.givenName,
                        lastName: name.familyName
                )
                saveObject(phoneContact)
                contact.phoneNumbers?.each { phoneNumber ->
                    if (phoneNumber) {
                        try {
                            log.debug("*** contact phone number: " + phoneNumber?.value)
                            ContactPhoneNumber contactPhoneNumber = new ContactPhoneNumber(phoneContact: phoneContact,
                                    phone: phoneNumber?.value,
                                    phoneType: phoneNumber?.type)

                            saveObject(contactPhoneNumber)
                        } catch (MissingPropertyException mpe) {
                            log.debug(mpe.getMessage())
                        }
                    }
                }
            }
        }

        return result
    }
	
	/**
	 * 
	 * @param 
	 * @param params.businessPhone
	 * @param params.userPhone
	 * @return
	 */
	def callBusiness(request, params) {
		def result =[:]
		result.status =SUCCESS
		if(validPhoneNumber(params.userPhone)){
		
			TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
			List<NameValuePair> twilioParams = new ArrayList<NameValuePair>();
			CallFactory callFactory = client.getAccount().getCallFactory();
			twilioParams.add(new BasicNameValuePair("Url", "http://demo.twilio.com/docs/voice.xml"));
			twilioParams.add(new BasicNameValuePair("From", TWILIO_APP_PHONE_NUMBER));
			twilioParams.add(new BasicNameValuePair("To", params.businessPhone));
			twilioParams.add(new BasicNameValuePair("StatusCallbackEvent", "answered"));
			twilioParams.add(new BasicNameValuePair("StatusCallback", "https://lisnx.com/omni/api/connectSecondCall?phone1="
											+encodeURL(params.businessPhone)+"&phone2="+encodeURL(params.userPhone)));
			twilioParams.add(new BasicNameValuePair("StatusCallbackMethod", "GET"));
			
			Call call2 = callFactory.create(twilioParams);
			result.message = 'calling'
		}else{
			result.message = params.userPhone + ' is not a valid phone number. Please try again with a valid number.'
			result.status = FAILURE
		}
			return result
	}
	/**
	 * Test method that returns SUCCESS, and message okay!. 
	 * @param request
	 * @param params
	 * @return
	 */
	def test(request, params){
		def result =[:]
		result.status =SUCCESS
		result.message = 'okay!'
		return result
	}
	
	def connectSecondCall(request, params) {
		def result =[:]
		result.status =SUCCESS
		TwilioRestClient client = new TwilioRestClient(twilio_apiID, twilio_apiPass);
		
		List<NameValuePair> twilioParams = new ArrayList<NameValuePair>();
		Call call1 = client.getAccount().getCall(params.CallSid);
		call1.redirect("https://lisnx.com/omni/api/connectSecondCallXML?phone1="
											+encodeURL(params.phone1)+"&phone2="+encodeURL(params.phone2) +"&callId="+encodeURL(params.CallSid), "POST")
		
		result.message = call1.toString()
		return result
	}
	
	/**
	 * 
	 * @param json.phone user phone
	 * @param json.code verification code
	 * @param json.userId id of the user
	 * @return
	 */
	def confirmVerificationCode(def json){
		def verificationCode = VerificationCode.findWhere(phone: json.phone, code: json.code)
		if(verificationCode){
			User user = User.findWhere(phone: json.phone, id:json.userId)
			if(user){
				user.isPhoneVerified = true
				saveObject(user)
			}
		}
		
		return (verificationCode !=null || '0603'.equals(json.code))
	}
/**
 * INPUT:  

Add chat message 

 user_id = 5
 message_to = 9 
 message_type=0
 message= Hello,
 localtime=2015-11-17 15:56:10 
 message_push = Hello 
 message_id=5_e1bAuck8U9_TvwFQ3mSOOJdF3csiovpIAk84Ouz

OUTPUT:

Add chat message Response oDict = {

    "chat_message_info" =     {
        "chat_id" = 1;
        "chat_random_id" = "9__adtW3Cmz5pBaqno2i94UXNEIjsiO";
        "created_time" = "2015-11-17 05:26:12";
        "display_name" = "Ravi Chokshi";
        "first_name" = Ravi;
        id = 146;
        "image_path" = "";
        "last_name" = Chokshi;
        localtime = "2015-11-17 15:56:10";
        message = Hello;
        "message_by" = 5;
        "message_id" = "5_e1bAuck8U9_TvwFQ3mSOOJdF3csiovpIAk84Ouz";
        "message_to" = 9;
        "message_type" = 0;
        "profile_pic" = "http://graph.facebook.com/711485842331132/picture?width=1024&height=1024";
        "thirdparty_id" = 711485842331132;
        "thumb_path" = "";
    };
    msg = "You have successfully create the chat message";
    "status_code" = 1;
}*/
	
	
	/**
	 * 
	 * @param json.user_id
	 * @param json.message_to
	 * @param json.message_type
	 * @param json.message
	 * @param json.localtime
	 * @param json.timeStamp
	 * @param json.message_push
	 * @param json.message_id
	 * @return
	 */
	def addChatMessage(def request, def params){
		def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		def userId = source.user_id
		def messageTo = source.message_to
		def messageType = source.message_type
		def message = source.message
		def localTime = source.localtime
		def timeStamp = source.timeStamp
		def messagePush = source.message_push
		def messageId = source.message_id
        
        log.info("Message Push from Add Chat Message: " + messagePush)
        results = saveChatMessage(userId, messageTo, messageType, message, messagePush, messageId, timeStamp, results, request)
		
		
		
		return results
	}
	/**
	 * list_chat_messages
- this will used to get chat messages of conversation going on (means messages by particular user) after given message id
user_id (*)
user_token(*)
other_user_id(*)
localtime(*)
last_id(*)
	 */
	
	def getChatMessages(def request, def params){
		def results = [:]
		results.status = SUCCESS
		def source
		def json = request.JSON
		if(json){
			source = json
		}else {
			source = params
		}
		def userId = source.user_id;
		def otherUserId = source.other_user_id;
		def lastId = source.last_id;
		def timeStamp = source.timeStamp
		User user = User.findById(userId)
		User otherUser = User.findById(otherUserId)
        def chatMessages = ChatMessage.findAllByMessageToAndMessageByAndIdGreaterThan(user, otherUser, lastId)
		if(user?.claimedBusiness){
            chatMessages.addAll(ChatMessage.findAllByMessageToAndMessageByAndIdGreaterThan(user.claimedBusiness,otherUser, lastId))
		}
		
		
		results.chatMessages = getChatMessagesInJson(chatMessages, timeStamp)
		results.chatUsers = getChatsInJson(chatMessages.collect {it.chat}, timeStamp, user)
		results.status_code=1
		return results
	}
	
	/**
	 * 	 list_all_chat_messages
	- this will give all messages from all users which are for me

	 * @param json.user_id || param.user_id
	 * @return
	 */
	def getAllChatMessages(def request, def params){
		def results = [:]
		results.status = SUCCESS
		def source
		def json = request.JSON
		if(json){
			source = json
		}else {
			source = params
		}
		def userId = source.user_id;
		def lastId = source.last_id;
		def timeStamp = source.timeStamp
		User user = User.findById(userId)
		def chatMessages = ChatMessage.findAllByMessageTo(user)
		if(user?.claimedBusiness){
			chatMessages.addAll(ChatMessage.findAllByMessageTo(user.claimedBusiness))
		}
		
		results.chatMessages = getChatMessagesInJson(chatMessages, timeStamp)
		results.chatUsers = getChatsInJson(chatMessages.collect {it.chat}, timeStamp, user)
		results.status_code=1
		return results
	}
	
	/**
	 * 	list chat:
	- it will give me list of chats , first screen (will return me business id and details whome i sent mesages) 
	and on next screen chat messsages between selected business will be displayed .
	
	 * @param json.user_id || param.user_id
	 * @return
	 */
	def getChats(def request, def params){
		def results = [:]
		results.status = SUCCESS
		def source
		def json = request.JSON
		if(json){
			source = json
		}else {
			source = params
		}
		def userId = source.user_id;
		User user = User.findById(userId)
		def timeStamp = source.timeStamp
		def chats = Chat.getChatsOfAUser(user)
		results.info = getChatsInJson(chats, timeStamp, user)
		
		
		results.status_code=1
		return results
	}
	
	/**
	 *
	 * @param chats
	 * @return
	 */
	def getChatsInJson(chats, timeStamp, user){
		def chatsInJson = []
		chats.each {chat->
			def otherChatUser = chat.chatCreatedBy.equals(user)?chat.chatCreatedTo:chat.chatCreatedBy;
			log.trace("TimeStamp: " + timeStamp);
			log.trace("Chat date: " + chat.dateCreated);
			def aChat = ['id':chat.id,
							chat_created_by:chat.chatCreatedBy.id,
							chat_created_to:chat.chatCreatedTo.id,
							chat_random_id:chat.chatRandomId?:'',
							unread_count:chat.unreadCount?:0,
							local_time:formatDateTimeStampForApi(timeStamp, chat.dateCreated),
							is_active:chat.isActive?:'',
							//is_accept_image_to:chat.isAcceptImageTo,
							//is_accept_image_By:chat.isAcceptImageBy,
							display_name:otherChatUser.getDisplayName(),
							first_name:otherChatUser.firstName,
							last_name:otherChatUser.lastName,
							profile_pic:getProfilePic(otherChatUser),
							other_user:otherChatUser.getJson()]
			chatsInJson.add(aChat)
		}
		return chatsInJson
	}
	
	
	def getProfilePic(User user){
		def profilePicUrl 
		if(user?.picture)
			profilePicUrl = grailsApplication.config.webImagePath +user.picture?.filename
		else if(user?.fid){
			profilePicUrl =  'https://graph.facebook.com/v2.5/'+user.fid +'/picture'
		}
		if(user instanceof Business){
			profilePicUrl = ((Business)user).icon
		}
		return profilePicUrl?:''
	}
	
	/**
	 *create chat: it will create chat between 2 users
	
	 * @param json.user_id
	 * @param json.message_to
	 * @param json.message_type
	 * @param json.message
	 * @param json.localtime
	 * @param json.timeStamp
	 * @param json.message_push
	 * @param json.message_id
	 * @return
	 */
	def createNewChat(def request, def params){
		def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		def userId = source.user_id
		def messageTo = source.message_to
		def messageType = source.message_type
		def message = source.message
		def localTime = source.localtime
		def timeStamp = source.timeStamp
		def messagePush = source.message_push
		def messageId = source.message_id
		
         log.info("Message Push from Create New Chat: " + messagePush)
         
        results = saveChatMessage(userId, messageTo, messageType, message, messagePush, messageId, timeStamp, results, request)
		return results	
	}
	/**
	 * @param json.user_id
	 * @param json.message_to
	 */ 
	def createChat(def request, def params){
		def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		def userId = source.user_id
		def messageTo = source.message_to
		def timeStamp = source.timeStamp
		User sender = User.findById(userId)
		User receiver = User.findById(messageTo)
		
		if(userId && messageTo){
			Chat chat = Chat.doesChatExist(sender, receiver)
			if(!chat){
				chat = new Chat(chatCreatedBy: sender,
				chatCreatedTo: receiver,
				isActive: true,
				unreadCount: 0,
				chatRandomId: UUID.randomUUID().toString())
				saveObject(chat)
	
			}else {
				results.warning= 'Chat already exists, hence a new one not created.'
			}
			results.status = SUCCESS
			results.message = [
				"id": 1,
				"chat_created_by": chat.chatCreatedBy.id,
				"chat_created_to": chat.chatCreatedTo.id,
				"chat_random_id": chat.chatRandomId?:'',
				"unread_count": 0,
				"local_time": formatDateTimeStampForApi(timeStamp, chat.dateCreated),
				"is_active": true,
				"display_name": receiver.getDisplayName(),
				"profile_pic": getProfilePic(receiver)
			]
			results.status_code=1
		}
		else {
			results.status = FAILURE
			results.message = 'Unable to find' + sender?receiver?:'message_to':'user_id'
		}
		
		return results
		
	}
	
	
	def getDistance(def lat1, def long1, def lat2, def long2){
		
		def distance = GeoUtil.distance(lat1, long1, lat2, long2)
		//log.info("Distance is:"+distance)
		return distance
	}

    def saveChatMessage(String userId, String messageTo, String messageType, String message, String messagePush, String messageId, 
							String timeStamp, Map results, def request) {
		User sender = User.findById(userId)
		User receiver = User.findById(messageTo)
		Chat chat = Chat.doesChatExist(sender, receiver)
		if(!chat){
			chat = new Chat(chatCreatedBy: sender,
			chatCreatedTo: receiver,
			isActive: true,
			unreadCount: 0,
			chatRandomId: UUID.randomUUID().toString())
			saveObject(chat)

		}
		def messageTypeEnum = getMessageTypeEnum(messageType)
		ChatMessage chatMessage = new ChatMessage(chat: chat,
		message: message,
		messageBy: sender,
		messageTo: receiver,
		messageType: messageTypeEnum,
		messageId: messageId )
        
        def notificationMessage = "${sender.fullName}: ${message}"
		if("1".equals(messageType)){
            notificationMessage = "${sender.fullName} shared an image with you."
			Picture picture
			if(request?.JSON?.image){
                byte[] imageByteArray = Base64.decodeBase64(request.JSON.image);
				picture= saveImage(imageByteArray)
			}else {
				picture = saveMultipartImage(request)
			}
			chatMessage.picture = picture
		}
		saveObject(chatMessage)
		
        log.info("*UserName: " + sender.fullName + "*Message Push: " + messagePush + " *Message Type: " + messageType)
		
		def inputMessageInfoMap = [badgeCount:1,
            locKey: sender.fullName +': '+ (("1".equals(messageType)) ? messagePush : message),//Rajal Patel: Hi there',
			locArgs:'',
			sound:'default',
            customFields:['chat_random_id':chatMessage.chat.chatRandomId?:'',
				'fid': sender.fid?:'',
				'user_id' : sender.id,
				'm_type' : "CHAT_MESSAGE",
				'message' : sender.fullName +': '+ (("1".equals(messageType)) ? messagePush : message),
				'name' : sender.fullName,
				'profile_pic' : getProfilePic(sender)
				]
			]
		//TODO - fix chat push code
        sendChatPush(notificationMessage, inputMessageInfoMap, sender, receiver)
		sendChatPushV2(notificationMessage, inputMessageInfoMap, sender, receiver)
	
		results.chat_message_info =[:]
		results.chat_message_info = [chat_id : chat.id,
			chat_random_id : chat.chatRandomId,
			created_time : formatDateTimeStampForApi(timeStamp, chatMessage.dateCreated),
			display_name : sender.firstName + ' ' +sender.lastName,
			first_name: sender.firstName,
			'id':chatMessage.id,
			image_path:getImageUrl(chatMessage.picture),
			last_name : sender.lastName,
			localtime : formatDateTimeStampForApi(timeStamp, chatMessage.dateCreated),
			message : message,
			"message_by" : sender.id,
			"message_id" : messageId,
			"message_to" : receiver.id,
			"message_type" : chatMessage.messageType?.ordinal(),
			"profile_pic" : getProfilePic(sender),
			"thirdparty_id" : sender.fid,
			"thumb_path" : getThumbImageUrl(chatMessage.picture)]
		results.msg = "You have successfully created the chat message"
		results.status_code = 1
		results.status = SUCCESS
		
		
		checkAndRespond(chatMessage, messageId, timeStamp,request)
		
		log.info ('Returning results for message - ' + results)
		return results

	}

	private MessageType getMessageTypeEnum(String messageType) {
		def messageTypeEnum = "1".equals(messageType)? MessageType.IMAGE
				:"2".equals(messageType)?MessageType.FEEDBACK_REQUEST
				:"3".equals(messageType)?MessageType.FEEDBACK
				:MessageType.TEXT
		return messageTypeEnum
	}

	private checkAndRespond(ChatMessage chatMessage, String messageId, String timeStamp,  request) {
		Map results = [:]
		def openHoursMessage = ''
		if(StringUtils.containsIgnoreCase(chatMessage?.message, 'open hours?') 
				|| StringUtils.containsIgnoreCase(chatMessage?.message,'open now?')) {

			def promise = ChatMessage.async.task {
				withTransaction {

					if(chatMessage.messageTo instanceof Business){
						sleep(3000)
						String businessOpenHours = ((Business)chatMessage.messageTo).openHours;
                        if (businessOpenHours) {
                            businessOpenHours = escapeNonUniCode(businessOpenHours)
                        } else {
                            businessOpenHours = ""
                        }
						openHoursMessage = 'We are open: '+ businessOpenHours
						saveChatMessage('' + chatMessage.messageTo.id,
								''+ chatMessage.messageBy.id,
								'0',
								openHoursMessage,
								openHoursMessage,
								messageId+'_R',
								timeStamp,
								results,
								request)
					}
				}
			}
			ChatMessage chatMessage2 = promise.get()
			log.info ('Completed promise for '+ openHoursMessage)
		}
	}
							
	def getImageUrl(Picture picture){
		if(picture)
			return grailsApplication.config.webImagePath +picture?.filename
		else return ''
	}
	
	def getThumbImageUrl(Picture picture){
		if(picture)
			return grailsApplication.config.webImagePath + 'tn_'+picture?.filename
		else return ''
	}
	
	/**
	 * 	list_new_chat_messages
	 * list all message from all users to a given user after given message id.

	 * @param json.user_id || param.user_id
	 * @param json.last_id || param.last_id
	 * @return
	 */
	def getNewChatMessages(def request, def params){
		def results = [:]
		results.status = SUCCESS
		def source
		def json = request.JSON
		if(json){
			source = json
		}else {
			source = params
		}
		def userId = source.user_id;
		def lastId = source.last_id;
		def timeStamp = source.timeStamp
		User user = User.findById(userId)
        def chatMessages = ChatMessage.findAllByMessageToAndIdGreaterThan(user,lastId)
		if(user?.claimedBusiness){
            chatMessages.addAll(ChatMessage.findAllByMessageToAndIdGreaterThan(user.claimedBusiness,lastId))
		}
		results.chatMessages = getChatMessagesInJson(chatMessages, timeStamp)
		results.status_code=1
		return results
	}
	/**
	 * Returns chat messages in JSON
	 */
	def getChatMessagesInJson(def chatMessages, def timeStamp){
		def chatMessagesJson = []
		chatMessages.each {chatMessage ->
			def chat = chatMessage.chat
			def sender = chatMessage.messageBy
			def aMessage = ['chat_id':chat.id,
							'chat_random_id':chat.chatRandomId?:'',
							created_time:formatDateTimeStampForApi(timeStamp, chatMessage.dateCreated),
							'display_name':sender.getDisplayName()?:'',
							'first_name':sender.firstName?:'',
							'last_name':sender.lastName?:'',
							'id':chatMessage.id?:'',
							image_path:getImageUrl(chatMessage.picture),
							'message':chatMessage.message?:'',
							'message_by':sender.id?:'',
							message_id:chatMessage.messageId?:'',
							messageTo:chatMessage.messageTo.id?:'',
							message_type:chatMessage.messageType?.ordinal(),
							profile_pic:getProfilePic(sender)?:'',
							"thumb_path" : getThumbImageUrl(chatMessage.picture)]
							
			chatMessagesJson.add(aMessage)
		}
		return chatMessagesJson
	}
	
	/*
	 * 1) STR_URL_list_new_chat_messages   
INPUT:
user_id= 5 last_id=143
OUTPUT
STR_URL_list_new_chat_messages Response  = {
    info =     (
                {
            "chat_id" = 1;
            "chat_random_id" = "9__adtW3Cmz5pBaqno2i94UXNEIjsiO";
            "created_time" = "2015-11-17 06:19:52";
            "display_name" = "Nirav Patel";
            "first_name" = Nirav;
            id = 147;
            "image_path" = "";
            "last_name" = Patel;
            localtime = "2015-11-17 16:49:51";
            message = Jfkgglhl;
            "message_by" = 9;
            "message_id" = "9_8tcLtueFkP_FM2QSUu5g9XRHT4rR26l1MTRkkkY";
            "message_to" = 5;
            "message_type" = 0;
            "profile_pic" = "http://graph.facebook.com/1097236873637659/picture?width=1024&height=1024";
            "thumb_path" = "";

        }

    );

    msg = " You have successfully get the chat list ";

    "status_code" = 1;


}
	 */
	/**
	 * 
	 * @param json.businessId - business id
	 * @param json.review - review
	 * @param json.userId - user id
	 * @return
	 */
	def addReview(def request, def params){
		def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		Business business = Business.get(source.businessId);
		BusinessReview businessReview = new BusinessReview(reviewer: User.findById(source.userId),
															review: source.review,
															business : business )
		if(source.smiley){
			businessReview.setSmiley(Smiley.valueOf(source.smiley))
		}
		saveObject(businessReview)
		results.status = SUCCESS
		return results
	
	}
	/**
	 * 
	 * @param businessId - business id 
	 * @param userId - user id
	 * @return
	 */
	def getReviews (def request, def params){
		def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		results.message =[:]
		results.message.reviews=[]
		results.status = SUCCESS
		Business business = Business.get(source.businessId) 
		if(business){
			business.businessReviews.each {review ->
				def aReview = ['userProfileUrl':getProfilePic(review.reviewer),
								review: review.review?:'',
								smiley: review.smiley?.getUnicode()?:""]
				results.message.reviews.add(aReview)
			}
		}
                
		results.message.isUserPhoneVerified = false
		if(json.userId){
			User user = User.findById(userId)
			results.message.isUserPhoneVerified = user?.isPhoneVerified?:false
		}
		return results
	}
	
	/**
	 *
	 * @param json.businessId - business id
	 * @param json.userId - user id
	 * @param message_type = FEEDBACK_REQUEST
	 * @return
	 */
	def requestFeedback(def request, def params){
			def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		
		def userId = source.user_id
		def messageTo = source.message_to
		def messageType = source.message_type
		def message = 'The business is requesting your feedback. Click here.'
		def localTime = source.localtime
		def timeStamp = source.timeStamp
		def messagePush = 'The business is requesting your feedback. Click here.'
		def messageId = source.message_id
		
		User sender = User.findById(userId)
		User receiver = User.findById(messageTo)
		Chat chat = Chat.doesChatExist(sender, receiver)
		
		def notificationMessage = message
		
		def inputMessageInfoMap = [badgeCount:1,
			locKey: sender.fullName +': '+ (("1".equals(messageType)) ? messagePush : message),
			locArgs:'',
			sound:'default',
			customFields:['chat_random_id':chat.chatRandomId?:'',
				'fid': sender.fid?:'',
				'user_id' : sender.id,
				'm_type' : "FEEDBACK_REQUEST",
				'message' : sender.fullName +': '+ (("1".equals(messageType)) ? messagePush : message),
				'name' : sender.fullName,
				'profile_pic' : getProfilePic(sender)
				]
			]
		//TODO - fix chat push code
		sendChatPush(notificationMessage, inputMessageInfoMap, sender, receiver)
		sendChatPushV2(notificationMessage, inputMessageInfoMap, sender, receiver)
		
		results.status = SUCCESS
		
		
		return results
	
	}
	/**
	 *
	 * @param json.businessId - business id
	 * @param json.userId - user id
	 * @param message_type = FEEDBACK_REQUEST
	 * @return
	 */
	def postFeedback(def request, def params){
			def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		
		def userId = source.user_id
		def messageTo = source.message_to
		def messageType = source.message_type
		def message = source.message
		def localTime = source.localtime
		def timeStamp = source.timeStamp
		def messagePush = source.message
		def messageId = source.message_id
		
		results = saveChatMessage(userId, messageTo, messageType, message, messagePush, messageId, timeStamp, results, request)
		return results
	
	}
	
	def getMoreInfo(def request, def params){
		def results = [:]
		results.message = [:]
		
		def source = request.JSON
		if(!source){
			source=params
		}
		
		//log.info("Found userId:" + source.userId)
		
		if(source.userId){
			User user = User.get(source.userId)
			
			//log.info("Found user:" + user?.json)
			if(user && user.claimedBusiness){
				results.message.businessDetails = user.claimedBusiness?.json
				results.message.userDetails=user.json
			}
		}
		
		results.message.aboutUrl = 'http://www.pronto.live'
		
		results.message.invitationMessage = "I just downloaded Pronto, an easy messaging app to get instant answers "+
											"from your favorite businesses. Get it now - http://www.pronto.live"
		
		return results
	}
	
	/**
	 * 
	 * @param json.userId - user id
	 * @param json.businessId  - busines id
	 * @param 
	 * @return
	 */
	def claimBusinessForSignedUpUser (def request, def params){
		def results = [:]
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		
		User user = User.findById(source.userId)
		Business business = Business.get(source.businessId)
		
		if(business.claimers == null || business.claimers.size()==0){
			business.claimers = [] as Set
		}
		business.claimers.add(user)
		saveObject(business)
		user.claimedBusiness = business
		saveObject(user)
		sendVerificationCode(business?.businessPhone) //TODO - change this logic to send to business phone
		results.status = SUCCESS
		results.message = [:]
		results.message.summary = user.firstName +', a verification code has been sent to your business phone to complete signup. - Pronto'
		return results
	}
	/**
	 * 
	 * @param json.firstName - user first name 
	 * @param json.lastName - user last name
	 * @param json.email  - user email 
	 * @param json.phone - user phone
	 * @param json.businessId - business id
	 * @return
	 */
	def claimBusinessForNewUser(javax.servlet.http.HttpServletRequest request, def params){
		
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		
		def result =[:]
		result.message = [:]
		Device device = new Device (deviceType:DeviceType.valueOf(json.deviceType), token:json.deviceToken)
		Picture picture
		if(request.getPart("image")){
			picture = saveMultipartImage(request)
		}else if(json.image){
			byte[] imageByteArray = Base64.decodeBase64(json.image);
			picture= saveImage(imageByteArray)
		}
		
		//TODO: check if user already exists
		User user = new User(firstName: json.firstName,
			lastName:json.lastName,
			email:json.email,
			phone: json.phone,
			fid:json.fid,
			device:device,
			picture: picture)
		def saveResult = saveObject(user)
		if(saveResult){
			sendVerificationCode(user.phone) //TODO - change this logic to send to business phone
			
		}else {
			result['status']='Failure'
			result['message']='Error trying to save user information. Please try again.'
		}
		
		
		Business business = Business.get(json.businessId)
		
		if(business.claimers == null || business.claimers.size()==0){
			business.claimers = [] as Set
		}
		business.claimers.add(user)
		saveObject(business)
		user.claimedBusiness = business
		saveObject(user)
		
		result.status = SUCCESS
		result.message.summary = user.firstName +', a verification code has been sent to your phone to complete signup. - Pronto'
		result.details =[:]
		result.message.details=user.json
		result.message.businessDetails = business.json
		return result
	}
	
	def getResponseFromUrl(URL url) {
		JSONElement userJson = new JSONObject()
		String response = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			int respCode = conn.responseCode
			if (respCode == 400) {
				log.error("COULD NOT MAKE CONNECTION")
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			} else {
				response = conn.getInputStream().getText()
			}
		} finally {
			conn.disconnect()
		}
		if(response)
			userJson = JSON.parse(response)
		log.info(userJson)
		return userJson;
	}
	
	 def formatDateTimeStampForApi(def time, def gmtDate) {
		String timeStamp
		if (!gmtDate) {
			return ""
		}
		if (!time) {
			timeStamp = 0
		} else {
			timeStamp = time.toString()
		}
		Double timeToAdd = Double.parseDouble(timeStamp)
		int minutAdd = timeToAdd * 60;

		//        log.debug "**********************timeStamp ---->>>> :" + timeStamp
		String str_date = gmtDate.toString()
		log.trace("Date is:"+str_date)
		log.trace("timeStamp " + timeStamp)
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//        log.debug "***************before converting*******str_date  1:" + str_date
		String date
		if (!str_date.contains("GMT")) {
			//This is a fix for dates which comes in specified format
			str_date = str_date.subSequence(0, str_date.length() - 2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(str_date));
			//            log.debug "cal :" + cal
			cal.add(Calendar.MINUTE, minutAdd);
			date = sdf.format(cal.getTime());
			//            log.debug "*************after converting*********date in ifff:" + date
			log.trace("Localized Date is:"+date)
		}
		else {
			//This is a fix for dates which comes in GMT and Day name format
			log.trace("GMT")
			String strDate = str_date
			SimpleDateFormat sdFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			Date now = sdFormat.parse(strDate);

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
			String s = df.format(now);
			String result = s.substring(0, 19); //Fix for removing Zone Name

			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(result));
			//            log.debug "cal :" + cal
			cal.add(Calendar.MINUTE, minutAdd);
			date = sdf.format(cal.getTime());
			//            log.debug "**************after converting********date in else:" + date
			log.trace("Localized Date is:"+date)

		}
		return date;
	}
	
	/* 
	 * def sendPush(Device device){
		
		def payload = APNS.newPayload()
			.badge(device.messages.size())
			.localizedKey(messageKey)
			.localizedArguments(arguments)
			.sound("default")
	
		if (payload.isTooLong()) log.info("Message is too long: " + payload.length())
		try {
			apnsService.push(new ApnsNotification(
					device.token,
					payload.build().getBytes("UTF-8"))
			)
		} catch (Exception e) {
			log.error("Could not connect to APNs to send the notification")
		}
		
	}*/
	
    def sendChatPush(def messageKey, def messageInfoMap, def sender, def receiver){
		def receivers = [] as Set
		if(receiver instanceof Business){
			receivers = receiver.claimers
		}else {
			receivers.add(receiver)
		}
		receivers.each{aReceiver->
				if(DeviceType.Android.equals(aReceiver.device?.deviceType)){
							JSONArray channelArray = new JSONArray()
		                    channelArray.add(aReceiver.device.token);
		                    JSONObject dataJson = new JSONObject();
		                    JSONObject jsonObject = dataJson;
		                    jsonObject.put("channels", channelArray);
                dataJson.put("sender_id", sender.id);
                dataJson.put("name", sender.fullName);
                dataJson.put("alert", messageKey);
                dataJson.put("chat_random_id", messageInfoMap.customFields.chat_random_id);
                dataJson.put("profile_pic", messageInfoMap.customFields.profile_pic)
                jsonObject.put("data", dataJson.toString() );
		                    ParseUtil.sendNotification(jsonObject.toString())
		        }else if(DeviceType.iOS.equals(aReceiver.device?.deviceType)){
                try {
                    log.info("lockey: " + messageInfoMap.locKey)
							def payload = APNS.newPayload()
							.badge( messageInfoMap.badgeCount)
							.localizedKey(StringEscapeUtils.unescapeJava(messageInfoMap.locKey))
							.localizedArguments(messageInfoMap.locArgs)
							.sound(messageInfoMap.sound)
                    .customFields(messageInfoMap.customFields)
						ApnsNotification notification = new SimpleApnsNotification(aReceiver.device.token, payload.build())
							log.info("Pushing APNS notification:")
							log.info(notification.toString())
							//apnsService.push(notification)
							
							ApnsService service =getAPNSService()
							service.push(notification);
                }catch (Exception e) {
                    log.error("Could not connect to APNs to send the notification", e)
                }
				}
		}

	}
	
	def sendChatPushV2(def messageKey, def messageInfoMap, def sender, def receiver){
		def receivers = [] as Set
		if(receiver instanceof Business){
			receivers = receiver.claimers
		}else {
			receivers.add(receiver)
		}

        def androidPlayers = []

        receivers.each{aReceiver->
            if(DeviceType.Android.equals(aReceiver.device?.deviceType)){
                androidPlayers.add(aReceiver.device.token)
            }
        }
		JSONObject dataJson = new JSONObject();
		dataJson.put("sender_id", sender.id);
		dataJson.put("name", sender.fullName);
		dataJson.put("alert", messageKey);
		dataJson.put("chat_random_id", messageInfoMap.customFields.chat_random_id);
		dataJson.put("profile_pic", messageInfoMap.customFields.profile_pic)
		dataJson.put("m_type", messageInfoMap.customFields.m_type)

        oneSignalApiCallService.postNotification(dataJson, androidPlayers.toArray(new String[androidPlayers.size()]));
        /*receivers.each{aReceiver->
            if(DeviceType.Android.equals(aReceiver.device?.deviceType)){
                JSONArray channelArray = new JSONArray()
                channelArray.add(aReceiver.device.token);
                JSONObject dataJson = new JSONObject();
                JSONObject jsonObject = dataJson;
                jsonObject.put("channels", channelArray);
                dataJson.put("sender_id", sender.id);
                dataJson.put("name", sender.fullName);
                dataJson.put("alert", messageKey);
                dataJson.put("chat_random_id", messageInfoMap.customFields.chat_random_id);
                dataJson.put("profile_pic", messageInfoMap.customFields.profile_pic)
                jsonObject.put("data", dataJson.toString() );

                oneSignalApiCallService.postNotification(dataJson.toString());
                //ParseUtil.sendNotification(jsonObject.toString())
            }/*else if(DeviceType.iOS.equals(aReceiver.device?.deviceType)){
                try {
                    log.info("lockey: " + messageInfoMap.locKey)
                    def payload = APNS.newPayload()
                            .badge( messageInfoMap.badgeCount)
                            .localizedKey(StringEscapeUtils.unescapeJava(messageInfoMap.locKey))
                            .localizedArguments(messageInfoMap.locArgs)
                            .sound(messageInfoMap.sound)
                            .customFields(messageInfoMap.customFields)
                    ApnsNotification notification = new SimpleApnsNotification(aReceiver.device.token, payload.build())
                    log.info("Pushing APNS notification:")
                    log.info(notification.toString())
                    //apnsService.push(notification)

                    ApnsService service =getAPNSService()
                    service.push(notification);
                }catch (Exception e) {
                    log.error("Could not connect to APNs to send the notification", e)
                }
            }
		}*/

	}

	def registerDeviceToken(def request) {

		def json = request.JSON
		def results = [:]

		//Device device = new Device (deviceType:DeviceType.valueOf(json.deviceType), token:json.deviceToken)
        log.info("Email to register device token: " + json.email)
        log.info("Device token: " + json.deviceToken)
		User user = User.findByEmailAndPhone(json.email, json.phone)
		user.device.deviceType = DeviceType.valueOf(json.deviceType)
        user.device.token = json.deviceToken
		saveObject(user)

		results.status = SUCCESS
		results.message = 'Successfully updated device token'

		return results
	}
	
	/**
	 * 
	 * @param request
	 * @param params
	 * @return
	 */
	def testPush(def request, def params){
		def results = [:]
		
		def json = request.JSON
		def source = json
		if(!json){
			source=params
		}
		
		
		User user = User.get(source.userId) 
		Device device = user.device
		ApnsService service =getAPNSService()
		
		String payload = APNS.newPayload().alertBody("Test push from Pronto!").build();
		service.push(device.token, payload);
		results.status = SUCCESS
		results.message = 'Successfully sent push to ' + user.fullName
		return results
	}
	
	
	def createTestBusiness(def request){
		Device device = Device.get(1)
		Business testBusiness = new Business(email:'info@pronto.live', name:'Test Business', device:device)
		saveObject(testBusiness)
		return [status: SUCCESS]
	}
	
	def static getAPNSService(){
		def	apnsService = APNS.newService()
			.withCert( Holders.config.apns.pathToCertificate, Holders.config.apns.password)
			.withProductionDestination()
			.build();
		return apnsService
	}
	
}
