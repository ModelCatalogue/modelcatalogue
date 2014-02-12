package uk.co.mdc.pathways

import grails.converters.JSON
import grails.converters.XML

class LinkMarshaller {

	void register() {
		def marshaller = { Link link ->
				
			return [
                'id' : link?.id,
                'source': link?.source?.id,
                'target': link?.target?.id,
                'version' : link?.version,
			]
		}

        JSON.registerObjectMarshaller(Link) { Link link ->
            return marshaller(link)
        }

        XML.registerObjectMarshaller(Link) { Link link ->
            return marshaller(link)
        }
	}
	
}
