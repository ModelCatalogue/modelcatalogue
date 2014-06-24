package uk.co.mdc.utils.importers

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.dataarchitect.ImportRow

class RelationshipImporterService {

    static transactional = true
    def relationshipService

    def importRelationships(ArrayList headers, ArrayList rows){
        def errorMessages = []
        rows.each { def row ->
            def source = PublishedElement.findByModelCatalogueId(row[0])
            def relationshipType = RelationshipType.findByNameIlike(row[1])
            def destination = PublishedElement.findByModelCatalogueId(row[2])
            if (source && relationshipType && destination) {
                try {
                    if(relationshipType.validateSourceDestination(source, destination)==null) {
                        relationshipService.link(source, destination, relationshipType)
                    }else{
                        errorMessages.add("could not create relationship between ${source} and ${destination} with relationship type: ${relationshipType} ")
                    }
                } catch (Exception e) {
                    errorMessages.add("could not create relationship between ${source} and ${destination} with relationship type: ${relationshipType} ")
                }
            }else if(source && destination){
                try {
                    relationshipType = new RelationshipType(name: row[1], sourceClass: PublishedElement, destinationClass: PublishedElement, sourceToDestination: row[1], destinationToSource: row[1]).save(flush:true)
                    relationshipService.link(source, destination, relationshipType)
                } catch (Exception e) {
                    errorMessages.add("could not create relationship between ${source} and ${destination} with relationship type: ${relationshipType} ")
                }
            }else{
                if(!source){errorMessages.add("could not find source element: ${row[0]}")}
                if(!relationshipType){errorMessages.add("could not find RelationshipType: ${row[1]}")}
                if(!destination){errorMessages.add("could not find destination element: ${row[2]}")}
            }
        }
        return errorMessages
    }
}


