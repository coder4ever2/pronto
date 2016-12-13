package omniapi

import java.util.Date;

class VerificationCode {
	
	String phone
	String code
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp

    static constraints = {
		phone nullable:false
		code nullable:false
    }
}
