package omniapi

import java.util.Date;

class PhoneContact {
	
	AlexaUser alexaUser
	String firstName
	String lastName
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp

    static constraints = {
		alexaUser nullable:true
		firstName nullable:true 
		lastName nullable:true
		
    }
}
