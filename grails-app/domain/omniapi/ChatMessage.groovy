package omniapi

import java.util.Date;

class ChatMessage {
	enum MessageType {
		TEXT, IMAGE, FEEDBACK_REQUEST, FEEDBACK
	}
	Chat chat
	String messageId
	String message
	User messageBy
	User messageTo
	MessageType messageType
	String imagePath
	String thumbPath
	Date localTime
	Picture picture
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	
	
	static belongsTo = [chat: Chat]
	

    static constraints = {
		chat nullable:false
		messageId nullable:true
		message nullable:true
		messageBy nullable: false
		messageTo nullable:false
		messageType nullalbe: false
		imagePath nullable: true
		thumbPath nullable:true
		localTime nullable:true
		picture nullable:true
		
    }
	
	static mapping = {
		message(type: 'text')
	}
}