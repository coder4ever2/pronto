import groovy.xml.StreamingMarkupBuilder

eventWebXmlEnd = {String tmpfile ->

    def root = new XmlSlurper().parse(webXmlFile)
    root.appendNode {
        'listener' {
            'listener-class' (
                    'omniapi.MyServletContextListenerAnnotated'
            )
        }
    }
    root.appendNode {
        'listener' {
            'listener-class' (
                    'omniapi.MyServletChatListenerAnnotated'
            )
        }
    }
    webXmlFile.text = new StreamingMarkupBuilder().bind {
        mkp.declareNamespace(
                "": "http://java.sun.com/xml/ns/javaee")
        mkp.yield(root)
    }
}