package omniapi

import grails.transaction.Transactional

@Transactional
class SeoService {
	
	def apiService

    def getSitemap() {
		def businesses = Business.withCriteria{
			ge('id',1L)
		}
		def sitemap =''
		businesses.each(){Business b->
			sitemap+=" http://pronto.ai/b/"+b.getRelativeURL() +"\n" 
		}
		log.warn(sitemap.toString())
		return sitemap
			
    }
}
