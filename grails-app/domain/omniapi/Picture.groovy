package omniapi

import org.codehaus.groovy.grails.commons.ConfigurationHolder


class Picture {
	Date dateCreated
	Date lastUpdated
	String originalFilename
	String mimeType
	Integer fileSize = new Integer(0)
	String filename
	String filePath
	Boolean hasThumbNail = false

	static constraints = {
		hasThumbNail nullable:true
	}
	def getProfilePic(){
		if(this.filename)
			return ConfigurationHolder.config.webImagePath +this.filename
		else if(this.user?.fid){
			return 'https://graph.facebook.com/v2.5/'+this.user.fid +'/picture'
		}
	}
}
