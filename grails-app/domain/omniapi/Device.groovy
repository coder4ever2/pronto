package omniapi


class Device {
	
	String token
	enum DeviceType {
		iOS, Android, Windows, Other
	}
	DeviceType deviceType
	User user
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	static belongsTo = [user: User]
	
    static constraints = {
		user(nullable:false)
		deviceType(nullable:false)
		token(nullable:false)	
    }
}
