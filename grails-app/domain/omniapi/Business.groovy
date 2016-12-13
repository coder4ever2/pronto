package omniapi

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

class Business extends User{
	
    String name
    Address address
    String googleId
    String icon
    String openHours
    String businessPhone
    String addressString
    String firstName = "My"
    String lastName = "Business"
    String email = 'info@omni.com'
    String phone = 'Phone'
    static hasMany = [businessReviews:BusinessReview, claimers:User]
    Date dateCreated // grails will auto timestamp
    Date lastUpdated // grails will auto timestamp
    String latitude
    String longitude
    String website
	String photoReference = ''
	String relativeURL
	String googleRating
	String googleBusinessTypes
	Boolean fetchedFullBusinessDetails = false
        
    static mapping = {
        businessReviews sort: 'lastUpdated', order: 'desc'
    }	
	
    static constraints = {
        name nullable: false
        address nullable: true
        googleId nullable:true
        icon nullable:true
        openHours nullable:true
        businessPhone nullable:true
        addressString nullable:true
        latitude nullable:true
        longitude nullable:true
        website nullable:true
		photoReference nullable:true
		relativeURL nullable:true
		googleRating nullable:true
		googleBusinessTypes nullable:true
		fetchedFullBusinessDetails nullable:true
		
    }
    def getJson(){
        def business = [:]
        business.businessId=id
        business.name = name
        business.lastName = lastName
        business.email = email
        business.phone = businessPhone?:''
        business.fid = fid?:''
        business.icon = icon?:''
        business.openHours = openHours?:''
        business.addressString = addressString?:'';
        business.claimers = claimers?.collect{it.id}?:''
        business.externalBusinessId = googleId
        business.latitude = latitude?:''
        business.longitude = longitude?:''
        business.website = website?:''
		business.photoReference = photoReference?:''
        business.noOfReviews= businessReviews?.size()?:0
        business.isBusiness = true
		business.claimers = []
		claimers.each { user ->
			business.claimers.add(user.json)
			
		}
        return business
    }
    def getBusinessId(){
        return googleId
    }
        
    def getFullName(){
        return name
    }
	
	def getRelativeURL(){
		if(!relativeURL){
			def nameAlphaNumeric = name.replaceAll("[^A-Za-z0-9]", "")
			String relativeURL = URLEncoder.encode(nameAlphaNumeric+'_'+id, 'UTF-8')
			this.relativeURL = relativeURL
			this.save()
			return relativeURL
		}
		else return relativeURL
	}
	def getPhotoReferenceURL(){
		if(!StringUtils.isEmpty(photoReference)){
			return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&"+ ApiService.googleApiKeyAttribute + '&photoreference='+photoReference
		}
		else return '';
	}
    
}
