package omniapi

import java.util.Date;

import omniapi.ApiService.AlexaIntent;

class AlexaEvent {
	
	AlexaUser alexaUser
	String phone
	AlexaIntent alexaIntent
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp

    static constraints = {
		alexaUser nullable:true
		phone nullable:true
		alexaIntent nullable:true
		dateCreated nullable:true
		lastUpdated nullable:true
    }
}
