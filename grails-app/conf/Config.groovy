// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.adhoc.includes = ['/images/**', '/css/**', '/js/**', '/plugins/**']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
 development {
        pictureLocation = "C:/Installed/ApacheSoftwareFoundation/Tomcat7/webapps/user_images/"
        promoImageLocation='C:/Installed/ApacheSoftwareFoundation/Tomcat7/webapps/promo-images/'
        grails.logging.jul.usebridge = true
    }
    test {
        pictureLocation = "/var/lib/tomcat7/webapps/test_user_images/"
        promoImageLocation ='/var/lib/tomcat7/webapps/test_promo-images/'
    }
    production {
        pictureLocation = "/var/lib/tomcat7/webapps/user_images/"
        promoImageLocation ='/var/lib/tomcat7/webapps/promo-images/'
        grails.logging.jul.usebridge = false
		webImagePath ="http://www.lisnx.com/user_images/"
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

environments {
	development {
		apns {
			//	pathToCertificate = "/opt/certificates/Certificates_Distribution_Lisnx.p12"
			//pathToCertificate = "C:/SomeStuff/LISNx/LisnxRepo/samyaa_api/Certificates_Distribution_Lisnx.p12"
			pathToCertificate = "/opt/certificates/iOSDistributionCertificate.p12"
			password = "lisnx"
			//	password = ""
			environment = "production"
		}
	}
	test {
		apns {
		//	pathToCertificate = "/opt/certificates/Certificates_Distribution_Lisnx.p12"
						pathToCertificate = "/opt/certificates/Certificates_Dev_Lisnx_2014.p12"
						password = ""
		//	password = "lisnx"
			environment = "sandbox"
		}
	}

	production {
		apns {
		//	pathToCertificate = "/opt/certificates/Certificates_Distribution_Lisnx.p12"
						pathToCertificate = "/opt/certificates/Certificates_APNS_OMNI_Production.p12"
						password = ""
			environment = "production"
		//                environment = "sandbox"
		}
	}
}

// log4j configuration
/*log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}*/
log4j = {
	// Example of changing the log pattern for the default console
	// appender:
	//
	debug 'omniapi'
	appenders {
		console name: 'stdout', layout: pattern(conversionPattern: '%d{MM-dd-yy HH:mm:ss,SSS} %5p %c{1} - %m%n')
	}
	debug 'grails'
	info 'grails'
	trace 'grails'
	error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'org.springframework',
			'org.hibernate',
			'net.sf.ehcache.hibernate'

	warn 'org.mortbay.log'
	environments {
		production {
			// Override previous setting for 'grails.app.controller'
			info 'grails'
		}
	}
}
twilio {
	// Enter your host address
	host = 'https://api.twilio.com'
	apiID = ''//appId
	apiPass = ''//appPass
	smsUrl = '/2010-04-01/Accounts/' + apiID + '/Messages.json'
	number = "+14154231013"
}
pictureLocation = "/var/lib/tomcat7/webapps/user_images/"
webImagePath ="http://www.lisnx.com/user_images/"
pictureWidth = 400
environments {
	development {
		pictureLocation = "/tmp/"
	}
}
cors.headers = ['Access-Control-Allow-Origin': '*', 
	'Access-Control-Allow-Methods': 'GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS',
'Access-Control-Allow-Headers': '*']
cors.enable.logging = true
//cors.url.pattern = ['/service1/*', '/service2/*']

