package omniapi

import java.util.Date;

class BusinessReview {
	
	static belongsTo = [business: Business]
	User reviewer
	String review
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	enum Smiley {
		Unhappy ("\\uD83D\\uDE20"), Happy ("\\uD83D\\uDE00"), OK ("\\uD83D\\uDE10")
		private String unicode;
		Smiley(String value) {
			this.unicode = value;
		}
		
		public String getUnicode() {
			return unicode;
		}
	}
	Smiley smiley

    static constraints = {
		reviewer nullable:false
		review nullable: true
		smiley nullable:true
    }
	
	static mapping = {
		review(type: 'text')
	}
}
