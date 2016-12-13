package omniapi

import org.apache.commons.lang3.StringUtils;

class BController {
	
	def seoService
	def apiService

    def index() { 
		def bId = params.name?.split("_")?.last();
		if(bId){
			def business = apiService.getBusiness(request, params, bId)
			if(business)
				render (view:"index", model:[business:business])
			else {
				render 'Not found'
			}
		}else if(params.name){
			def business = Business.findByRelativeURL(params.name)
			if(business)
				render (view:"index", model:[business:business])
			else {
				render 'Not found'
			}
		}else if(params.id){
			def business = apiService.getBusiness(request, params, params.id)
			if(business)
				render (view:"index", model:[business:business])
			else {
				render 'Not found'
			}
		}
		else {
			render 'Not found'
		}
	}
	def robots(){
		render """
			Sitemap: http://pronto.ai/sitemap.txt
			User-agent: *
			Disallow:
		"""
	}
	def sitemap(){
		def sitemap = seoService.getSitemap()
		log.warn(sitemap)
		render sitemap
	}
	
	def scrape(){
		def results = [:]
		def source = request.JSON
		def searchResultList = []
		def businesses = []
		if(!source){
			source = params
		}
		apiService.searchAndSaveBusinesses(request, params)
		
		render (view:"scrape", model:[businesses:searchResultList])
	}
	def search(){
		def businesses = []
		if(params.keyword)
			businesses = apiService.searchAndSaveBusinesses(request, params)
		render (view:"search", model:[businesses:businesses])
	}
}
