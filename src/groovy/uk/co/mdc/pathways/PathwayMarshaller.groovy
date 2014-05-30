package uk.co.mdc.pathways;

import grails.converters.JSON
import grails.converters.XML


public class PathwayMarshaller {

	void register() {
        def marshaller = { Pathway pathway ->

            return [
                'id' : pathway.id,
                'name': pathway?.name,
                'userVersion': pathway?.userVersion,
                'isDraft': pathway?.isDraft,
                'description'	: pathway?.description,
                'nodes' : pathway.getNodes(),
                'links' : pathway.getLinks(),
                'version' : pathway.version,
            ]
        }

        JSON.registerObjectMarshaller(Pathway) { Pathway pathway ->
            return marshaller(pathway)
        }

        XML.registerObjectMarshaller(Pathway) { Pathway pathway ->
            return marshaller(pathway)
        }
	}
}


