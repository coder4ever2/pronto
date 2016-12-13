package omniapi

import java.util.Date;

class ContactPhoneNumber {
	
	PhoneContact phoneContact
	String phone
	String phoneType
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
    static constraints = {
		phoneContact nullable:true
		phone nullable:true
		phone nullable:true
    }
}
