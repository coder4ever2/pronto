package omniapi

import java.util.Date;

class AlexaUser {
	
	String phone
	String alexaUserId
	Boolean isVerified = false
	String verificationCode
	String twilioVerificationCode
	String name
	String email
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
   
	

    static constraints = {
		alexaUserId nullable:true
		verificationCode nullable:true
		phone nullable:true
		name nullable:true
		email nullable:true
		dateCreated nullable:true
		lastUpdated nullable:true
		twilioVerificationCode nullable:true
    }
}
