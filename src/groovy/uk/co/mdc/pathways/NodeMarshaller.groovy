package uk.co.mdc.pathways

//'dataElements' : '[{"id" : "Data_Element_10", "description" : "How loud can you shout?"}]'      + node?.GetElementsJSON() + 
//node.peCollection.dataElementsCollections()
//

import grails.converters.JSON
import grails.converters.XML

//{"id" : "' + node.peCollection.id + '", "description" : "' + node.peCollection.description + '"}

class NodeMarshaller {
	void register() {
        def marshaller = { Node node ->

            return [
                'id':           node.id,
                'name' :        node.name,
                'description':  node.description,
                'x' :           node.x,
                'y' :           node.y,
                'version' :     node.version,
                'nodes' :       node.nodes,
                'links' :       node.links,
                'pathway':      node.pathway?.id,
                'forms':        node.forms,
                'dataElements': node.dataElements,
            ]
        }

        JSON.registerObjectMarshaller(Node) { Node node ->
            return marshaller(node)
        }

        XML.registerObjectMarshaller(Node) { Node node ->
            return marshaller(node)
        }
	}
}
