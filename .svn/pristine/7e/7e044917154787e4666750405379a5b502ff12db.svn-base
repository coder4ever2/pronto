package omniapi

import java.util.Date;

class Chat {
	
	User chatCreatedBy
	User chatCreatedTo
	String chatRandomId
	Integer unreadCount
	Date localTime
	Boolean isActive
	Boolean isAcceptImageTo
	Boolean isAcceptImageBy
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	

    static constraints = {
		chatRandomId nullable:true
		isAcceptImageBy nullable:true
		unreadCount nullable:true
		localTime nullable:true
		isActive nullable:true
		isAcceptImageTo nullable:true
		isAcceptImageBy nullable:true
    }
	
	def static doesChatExist(User user1, User user2) {
		Chat chat = Chat.findWhere(chatCreatedBy: user1, chatCreatedTo: user2)
		if (!chat) {
			chat = Chat.findWhere(chatCreatedBy: user2, chatCreatedTo: user1)
		}
		return chat
	}
	def static getChatsOfAUser(User user){
		def chats = Chat.createCriteria().list {
			or {
				eq("chatCreatedBy", user)
				eq("chatCreatedTo", user)
				eq("chatCreatedBy", user.claimedBusiness)
				eq("chatCreatedTo", user.claimedBusiness)
				
			}
		}
		return chats
	}
}
