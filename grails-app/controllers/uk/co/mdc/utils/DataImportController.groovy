package uk.co.mdc.utils

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtendibleElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN'])
class DataImportController {

	static allowedMethods = [importDataSet: "GET", ]
	
	def importNHICService, elasticSearchAdminService, elasticSearchService

    def index(){
        [nhicFiles: importNHICService.getNhicFiles()]
    }
    def importDataSet() { 
		def dataset = params.dataset
        def errors

		if(dataset == "nhic"){
            if(params.nhicFile){
                errors = importNHICService.singleImport(params.nhicFile)
            }else{
                errors =  importNHICService.importData()
            }
            if(errors.isEmpty()) {
                flash.message = "dataimport.complete"
                flash.default = "Process complete"
            }else{
                flash.message = "Process complete with errors: ${errors}"
                flash.default = "Process complete with errors"
                flash.errors = errors
            }
		}
        else{
            flash.message = "dataimport.paramError"
            flash.default = "Error: invalid dataset"
        }


        elasticSearchService.index(DataElement)
        elasticSearchService.index(ValueDomain)
        elasticSearchService.index(Model)
        elasticSearchService.index(ConceptualDomain)
        elasticSearchService.index(DataType)
        elasticSearchService.index(EnumeratedType)
        elasticSearchService.index(MeasurementUnit)
        elasticSearchService.index(ExtendibleElement)
        elasticSearchService.index(ExtensionValue)
        elasticSearchService.index(Relationship)
        elasticSearchService.index(RelationshipType)
        elasticSearchService.index(CatalogueElement)
        elasticSearchAdminService.refresh()

        flash.args = [dataset]
		render(view:"/dataImport/index")
	}

}
